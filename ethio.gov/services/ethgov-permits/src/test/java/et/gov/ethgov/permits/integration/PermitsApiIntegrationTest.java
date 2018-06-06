package et.gov.ethgov.permits.integration;

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
@Import(PermitsTestJwtConfig.class)
@Testcontainers
class PermitsApiIntegrationTest {

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
    void appliesReviewsIssuesActivatesAndCompletesPermit() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "type": "NEW_BUILDING",
                  "structureUse": "RESIDENTIAL",
                  "projectName": "Bole G+2 Residence",
                  "description": "Two-storey residential building",
                  "organizationId": "org_11111111-1111-4111-8111-111111111111",
                  "parcelId": "par_11111111-1111-4111-8111-111111111111",
                  "floors": 3,
                  "floorAreaSquareMeters": "350.00",
                  "estimatedCostEtb": "2500000.00",
                  "region": "Addis Ababa",
                  "zone": "Bole",
                  "woreda": "Bole",
                  "kebele": "03",
                  "locality": "Around Edna Mall"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/permits")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.permitId").exists())
                .andReturn();

        String permitId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.permitId");

        mockMvc.perform(get("/v1/permits/" + permitId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPLIED"))
                .andExpect(jsonPath("$.permitNumber").exists())
                .andExpect(jsonPath("$.parcelId").exists());

        mockMvc.perform(post("/v1/permits/" + permitId + "/review")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documentId\":\"doc_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/workflow")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"processInstanceId\":\"wpi_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/issue")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "validFrom": "2024-07-01T00:00:00Z",
                                  "validTo": "2026-07-01T00:00:00Z"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/suspend")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Unsafe scaffolding\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/reinstate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/permits/" + permitId + "/complete")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/permits/" + permitId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_permits.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.processInstanceId").exists())
                .andExpect(jsonPath("$.completedAt").exists());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.permits.applied.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
