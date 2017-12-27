package et.gov.ethgov.gateway.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.testing.containers.EthGovContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(GatewayTestJwtConfig.class)
@Testcontainers
class GatewayApiIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = EthGovContainers.postgis();

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("ethgov.identity.issuer", () -> "http://localhost:8081");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void rateLimitPolicyLifecycleAndEvaluate() throws Exception {
        String tenantId = TenantId.generate().value();
        String appId = "dap_11111111-1111-4111-8111-111111111111";
        String body =
                """
                {
                  "tenantId": "%s",
                  "name": "Partner app quota",
                  "subjectType": "APP",
                  "subjectKey": "%s",
                  "limitPerWindow": 2,
                  "windowSeconds": 60
                }
                """
                        .formatted(tenantId, appId);

        MvcResult created = mockMvc.perform(post("/v1/rate-limits")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rateLimitPolicyId").exists())
                .andReturn();

        String policyId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.rateLimitPolicyId");

        mockMvc.perform(put("/v1/rate-limits/" + policyId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "name": "Partner app quota v2",
                                  "limitPerWindow": 2,
                                  "windowSeconds": 60
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/rate-limits/" + policyId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write"))))
                .andExpect(status().isNoContent());

        String evaluateBody =
                """
                {
                  "tenantId": "%s",
                  "subjectType": "APP",
                  "subjectKey": "%s",
                  "actorId": "edge-gateway"
                }
                """
                        .formatted(tenantId, appId);

        mockMvc.perform(post("/v1/rate-limits/evaluate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evaluateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.hits").value(1));

        mockMvc.perform(post("/v1/rate-limits/evaluate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evaluateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hits").value(2));

        mockMvc.perform(post("/v1/rate-limits/evaluate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(evaluateBody))
                .andExpect(status().isTooManyRequests());

        mockMvc.perform(get("/v1/rate-limits/" + policyId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.gateway.ratelimit.created.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);

        Integer deniedAudits = jdbcTemplate.queryForObject(
                "select count(*) from gw_audit_entry where outcome = ?", Integer.class, "DENIED");
        assertThat(deniedAudits).isGreaterThanOrEqualTo(1);
    }

    @Test
    void resilienceAndAuditFlow() throws Exception {
        String tenantId = TenantId.generate().value();

        MvcResult created = mockMvc.perform(post("/v1/resilience-policies")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "tenantId": "%s",
                                  "dependencyName": "ethgov-payments",
                                  "failureRateThreshold": 50,
                                  "waitDurationSeconds": 30,
                                  "permittedCallsInHalfOpen": 5
                                }
                                """
                                        .formatted(tenantId)))
                .andExpect(status().isCreated())
                .andReturn();

        String resilienceId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.resiliencePolicyId");

        mockMvc.perform(post("/v1/resilience-policies/" + resilienceId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/resilience-policies/lookup")
                        .param("tenantId", tenantId)
                        .param("dependencyName", "ethgov-payments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        MvcResult audit = mockMvc.perform(post("/v1/audit-entries")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "tenantId": "%s",
                                  "actorId": "usr_ops",
                                  "action": "resilience.activate",
                                  "resourceType": "resilience_policy",
                                  "resourceId": "%s",
                                  "outcome": "SUCCESS",
                                  "detailJson": "post-incident note"
                                }
                                """
                                        .formatted(tenantId, resilienceId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.auditEntryId").exists())
                .andReturn();

        String auditId = com.jayway.jsonpath.JsonPath.read(
                audit.getResponse().getContentAsString(), "$.auditEntryId");

        mockMvc.perform(get("/v1/audit-entries")
                        .param("tenantId", tenantId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].auditEntryId").value(auditId));

        mockMvc.perform(post("/v1/resilience-policies/" + resilienceId + "/suspend")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"psp outage window\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/resilience-policies/" + resilienceId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelsDraftRateLimitPolicy() throws Exception {
        String tenantId = TenantId.generate().value();
        MvcResult created = mockMvc.perform(post("/v1/rate-limits")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "tenantId": "%s",
                                  "name": "Draft only",
                                  "subjectType": "ROUTE",
                                  "subjectKey": "/v1/opendata/catalog",
                                  "limitPerWindow": 1000,
                                  "windowSeconds": 60
                                }
                                """
                                        .formatted(tenantId)))
                .andExpect(status().isCreated())
                .andReturn();
        String policyId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.rateLimitPolicyId");
        mockMvc.perform(post("/v1/rate-limits/" + policyId + "/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"abandoned draft policy\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/rate-limits/" + policyId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gateway.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
