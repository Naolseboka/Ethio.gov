package et.gov.ethgov.land.integration;

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
@Import(LandTestJwtConfig.class)
@Testcontainers
class LandApiIntegrationTest {

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
    void registersTransfersEncumbersAttachesAndRetiresParcel() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "region": "Addis Ababa",
                  "zone": "Bole",
                  "woreda": "Bole",
                  "kebele": "03",
                  "locality": "Around Edna Mall",
                  "landUse": "RESIDENTIAL",
                  "tenureType": "LEASEHOLD",
                  "areaSquareMeters": "250.00",
                  "citizenId": "cit_11111111-1111-4111-8111-111111111111",
                  "titleDeedNumber": "TD-AA-2024-1",
                  "leaseReference": "LEASE-AA-9001",
                  "leaseExpiresAt": "2054-06-01T00:00:00Z"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/parcels")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parcelId").exists())
                .andReturn();

        String parcelId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.parcelId");

        mockMvc.perform(get("/v1/parcels/" + parcelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.parcelNumber").exists())
                .andExpect(jsonPath("$.tenureType").value("LEASEHOLD"));

        mockMvc.perform(put("/v1/parcels/" + parcelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "region": "Addis Ababa",
                                  "zone": "Bole",
                                  "woreda": "Bole",
                                  "kebele": "03",
                                  "locality": "Near Edna Mall",
                                  "landUse": "MIXED",
                                  "tenureType": "LEASEHOLD",
                                  "areaSquareMeters": "255.50",
                                  "titleDeedNumber": "TD-AA-2024-1",
                                  "leaseReference": "LEASE-AA-9001",
                                  "leaseExpiresAt": "2054-06-01T00:00:00Z"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/transfer")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"organizationId\":\"org_22222222-2222-4222-8222-222222222222\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/encumber")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Bank mortgage lien\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/clear-encumbrance")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"documentId\":\"doc_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/case")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"caseId\":\"cas_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/parcels/" + parcelId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/parcels/" + parcelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_land.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETIRED"))
                .andExpect(jsonPath("$.organizationId").exists())
                .andExpect(jsonPath("$.linkedCaseId").exists());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.land.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
