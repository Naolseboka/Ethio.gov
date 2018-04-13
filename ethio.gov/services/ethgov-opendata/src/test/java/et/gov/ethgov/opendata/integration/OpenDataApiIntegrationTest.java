package et.gov.ethgov.opendata.integration;

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
@Import(OpenDataTestJwtConfig.class)
@Testcontainers
class OpenDataApiIntegrationTest {

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
    void registersPublishesAddsResourceAndRetires() throws Exception {
        String tenantId = TenantId.generate().value();
        String datasetBody =
                """
                {
                  "tenantId": "%s",
                  "slug": "land-parcels-addis",
                  "title": "Addis Ababa land parcels",
                  "description": "Public land parcel registry extract for Addis Ababa",
                  "theme": "LAND",
                  "license": "OGL_ETHIOPIA",
                  "publisherOrgId": "org_11111111-1111-4111-8111-111111111111",
                  "contactEmail": "opendata@addis.gov.et",
                  "locale": "en"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/datasets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datasetBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.datasetId").exists())
                .andReturn();

        String datasetId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.datasetId");

        mockMvc.perform(put("/v1/datasets/" + datasetId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "title": "Addis Ababa land parcels v2",
                                  "description": "Updated public land parcel registry extract for Addis Ababa",
                                  "theme": "LAND",
                                  "license": "CC_BY",
                                  "publisherOrgId": "org_11111111-1111-4111-8111-111111111111",
                                  "contactEmail": "opendata@addis.gov.et",
                                  "locale": "am"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/datasets/" + datasetId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write"))))
                .andExpect(status().isNoContent());

        String resourceBody =
                """
                {
                  "tenantId": "%s",
                  "datasetId": "%s",
                  "title": "Parcels CSV",
                  "description": "Monthly CSV extract",
                  "format": "CSV",
                  "accessUrl": "https://opendata.addis.gov.et/parcels.csv",
                  "mediaType": "text/csv"
                }
                """
                        .formatted(tenantId, datasetId);

        MvcResult resourceCreated = mockMvc.perform(post("/v1/resources")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceId").exists())
                .andReturn();

        String resourceId = com.jayway.jsonpath.JsonPath.read(
                resourceCreated.getResponse().getContentAsString(), "$.resourceId");

        mockMvc.perform(get("/v1/resources/by-dataset/" + datasetId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceId").value(resourceId))
                .andExpect(jsonPath("$[0].format").value("CSV"));

        mockMvc.perform(get("/v1/datasets/lookup")
                        .param("tenantId", tenantId)
                        .param("slug", "land-parcels-addis")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"))
                .andExpect(jsonPath("$.license").value("CC_BY"));

        mockMvc.perform(post("/v1/resources/" + resourceId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"replaced by newer extract\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/datasets/" + datasetId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write"))))
                .andExpect(status().isNoContent());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.opendata.dataset.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }


    @Test
    void cancelsDraftDatasetAndAddsDocumentBackedResource() throws Exception {
        String tenantId = TenantId.generate().value();
        String datasetBody =
                """
                {
                  "tenantId": "%s",
                  "slug": "license-stats-draft",
                  "title": "License statistics draft",
                  "description": "Draft catalog entry for license issuance statistics",
                  "theme": "LICENSE",
                  "license": "PUBLIC_DOMAIN",
                  "locale": "en"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/datasets")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(datasetBody))
                .andExpect(status().isCreated())
                .andReturn();
        String datasetId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.datasetId");

        String resourceBody =
                """
                {
                  "tenantId": "%s",
                  "datasetId": "%s",
                  "title": "License PDF pack",
                  "format": "PDF",
                  "documentId": "doc_11111111-1111-4111-8111-111111111111",
                  "mediaType": "application/pdf"
                }
                """
                        .formatted(tenantId, datasetId);
        mockMvc.perform(post("/v1/resources")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(resourceBody))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/datasets/" + datasetId + "/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"duplicate catalog draft\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/datasets/" + datasetId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_opendata.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
