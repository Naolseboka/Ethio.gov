package et.gov.ethgov.gis.integration;

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
@Import(GisTestJwtConfig.class)
@Testcontainers
class GisApiIntegrationTest {

    private static final String ADDIS_POLYGON =
            "POLYGON((38.73 9.02,38.75 9.02,38.75 9.04,38.73 9.04,38.73 9.02))";

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
    void registersPublishesAndFindsByPoint() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "type": "ZONE",
                  "name": "Bole Zone",
                  "description": "Municipal planning zone",
                  "wkt": "%s"
                }
                """
                        .formatted(tenantId, ADDIS_POLYGON);

        MvcResult created = mockMvc.perform(post("/v1/features")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.featureId").exists())
                .andReturn();

        String featureId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.featureId");

        mockMvc.perform(get("/v1/features/" + featureId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.type").value("ZONE"));

        String update =
                """
                {
                  "name": "Bole Zone North",
                  "description": "Updated zone",
                  "wkt": "%s"
                }
                """
                        .formatted(ADDIS_POLYGON);
        mockMvc.perform(put("/v1/features/" + featureId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/features/" + featureId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/features/contains")
                        .param("tenantId", tenantId)
                        .param("longitude", "38.74")
                        .param("latitude", "9.03")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].featureId").value(featureId))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));

        mockMvc.perform(get("/v1/features/contains")
                        .param("tenantId", tenantId)
                        .param("longitude", "39.00")
                        .param("latitude", "9.03")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        mockMvc.perform(post("/v1/features/" + featureId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.write"))))
                .andExpect(status().isNoContent());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.gis.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }

    @Test
    void registersParcelBoundaryWithParcelId() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "type": "PARCEL_BOUNDARY",
                  "name": "Parcel Boundary A",
                  "wkt": "%s",
                  "parcelId": "par_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId, ADDIS_POLYGON);

        mockMvc.perform(post("/v1/features")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_gis.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.featureId").exists());
    }
}
