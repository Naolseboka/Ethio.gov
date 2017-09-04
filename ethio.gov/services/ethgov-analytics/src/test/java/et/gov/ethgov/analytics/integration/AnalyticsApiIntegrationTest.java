package et.gov.ethgov.analytics.integration;

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
@Import(AnalyticsTestJwtConfig.class)
@Testcontainers
class AnalyticsApiIntegrationTest {

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
    void registersPublishesRecordsAndVoidsObservation() throws Exception {
        String tenantId = TenantId.generate().value();
        String metricBody =
                """
                {
                  "tenantId": "%s",
                  "code": "LICENSE_ISSUED_COUNT",
                  "name": "Licenses issued",
                  "description": "Count of business licenses issued in period",
                  "category": "LICENSE",
                  "unit": "COUNT",
                  "aggregation": "SUM"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/metrics")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(metricBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metricDefinitionId").exists())
                .andReturn();

        String metricId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.metricDefinitionId");

        mockMvc.perform(put("/v1/metrics/" + metricId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "name": "Licenses issued (monthly)",
                                  "description": "Monthly count of business licenses issued",
                                  "category": "LICENSE",
                                  "unit": "COUNT",
                                  "aggregation": "SUM"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/metrics/" + metricId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write"))))
                .andExpect(status().isNoContent());

        String obsBody =
                """
                {
                  "tenantId": "%s",
                  "metricDefinitionId": "%s",
                  "value": "128",
                  "periodStart": "2024-05-01T00:00:00Z",
                  "periodEnd": "2024-05-31T23:59:59Z",
                  "dimensionsJson": "{\\"woreda\\":\\"bole\\"}",
                  "sourceReference": "rpt_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId, metricId);

        MvcResult obsCreated = mockMvc.perform(post("/v1/observations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(obsBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.observationId").exists())
                .andReturn();

        String observationId = com.jayway.jsonpath.JsonPath.read(
                obsCreated.getResponse().getContentAsString(), "$.observationId");

        mockMvc.perform(get("/v1/observations/by-metric/" + metricId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].observationId").value(observationId))
                .andExpect(jsonPath("$[0].value").value("128"));

        mockMvc.perform(post("/v1/observations/" + observationId + "/void")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"incorrect source extract\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/observations/" + observationId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VOIDED"));

        mockMvc.perform(get("/v1/metrics/lookup")
                        .param("tenantId", tenantId)
                        .param("code", "LICENSE_ISSUED_COUNT")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.analytics.metric.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }

    @Test
    void cancelsDraftMetric() throws Exception {
        String tenantId = TenantId.generate().value();
        String metricBody =
                """
                {
                  "tenantId": "%s",
                  "code": "REVENUE_ETB_COLLECTED",
                  "name": "Revenue collected",
                  "category": "REVENUE",
                  "unit": "ETB",
                  "aggregation": "SUM"
                }
                """
                        .formatted(tenantId);
        MvcResult created = mockMvc.perform(post("/v1/metrics")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(metricBody))
                .andExpect(status().isCreated())
                .andReturn();
        String metricId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.metricDefinitionId");
        mockMvc.perform(post("/v1/metrics/" + metricId + "/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"duplicate metric draft\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/metrics/" + metricId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_analytics.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
