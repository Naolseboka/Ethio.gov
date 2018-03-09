package et.gov.ethgov.licensing.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
@Import(LicensingTestJwtConfig.class)
@Testcontainers
class LicensingApiIntegrationTest {

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
    void appliesReviewsIssuesActivatesSuspendsAndRenewsLicense() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "type": "TRADE",
                  "businessName": "Bole Retail Shop",
                  "activityDescription": "General merchandise retail",
                  "organizationId": "org_11111111-1111-4111-8111-111111111111",
                  "region": "Addis Ababa",
                  "zone": "Bole",
                  "woreda": "Bole",
                  "kebele": "03",
                  "locality": "Around Edna Mall"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/licenses")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenseId").exists())
                .andReturn();

        String licenseId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.licenseId");

        mockMvc.perform(get("/v1/licenses/" + licenseId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.licenseNumber").exists());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/review")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documentId\":\"doc_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/workflow")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"processInstanceId\":\"wpi_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/issue")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "validFrom": "2024-07-01T00:00:00Z",
                                  "validTo": "2025-07-01T00:00:00Z"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/suspend")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Health inspection failed\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/reinstate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/licenses/" + licenseId + "/renew")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"validTo\":\"2026-07-01T00:00:00Z\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/licenses/" + licenseId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_licensing.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.processInstanceId").exists())
                .andExpect(jsonPath("$.validTo").value("2026-07-01T00:00:00Z"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.licensing.applied.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
