package et.gov.ethgov.search.integration;

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
@Import(SearchTestJwtConfig.class)
@Testcontainers
class SearchApiIntegrationTest {

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
    void indexesAndFindsByFullTextAndFuzzyName() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "sourceType": "CITIZEN",
                  "sourceId": "cit_11111111-1111-4111-8111-111111111111",
                  "title": "Abebe Kebede",
                  "summary": "Citizen in Bole subcity",
                  "bodyText": "Abebe Kebede lives in Addis Ababa Bole kebele 03",
                  "locale": "en",
                  "tagsJson": "[\\"citizen\\",\\"bole\\"]"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/search/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.searchDocumentId").exists())
                .andReturn();

        String documentId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.searchDocumentId");

        mockMvc.perform(get("/v1/search")
                        .param("tenantId", tenantId)
                        .param("q", "Abebe")
                        .param("sourceTypes", "CITIZEN")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].document.searchDocumentId").value(documentId))
                .andExpect(jsonPath("$[0].document.title").value("Abebe Kebede"));

        // fuzzy / trigram style query
        mockMvc.perform(get("/v1/search")
                        .param("tenantId", tenantId)
                        .param("q", "Abebe Kebede")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].document.sourceId")
                        .value("cit_11111111-1111-4111-8111-111111111111"));

        mockMvc.perform(put("/v1/search/documents/" + documentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "title": "Abebe Kebede Updated",
                                  "summary": "Updated citizen profile",
                                  "bodyText": "Abebe Kebede relocated within Addis Ababa",
                                  "locale": "en",
                                  "tagsJson": "[\\"citizen\\"]"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/search/documents/" + documentId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"citizen record archived\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/search")
                        .param("tenantId", tenantId)
                        .param("q", "Abebe")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.search.indexed.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }

    @Test
    void upsertsBySourceAndFiltersOrganization() throws Exception {
        String tenantId = TenantId.generate().value();
        String orgBody =
                """
                {
                  "tenantId": "%s",
                  "sourceType": "ORGANIZATION",
                  "sourceId": "org_22222222-2222-4222-8222-222222222222",
                  "title": "Blue Nile Trading PLC",
                  "bodyText": "Blue Nile Trading PLC TIN 0001234567 Addis Ababa",
                  "locale": "en"
                }
                """
                        .formatted(tenantId);
        mockMvc.perform(post("/v1/search/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orgBody))
                .andExpect(status().isCreated());

        // upsert same source
        mockMvc.perform(post("/v1/search/documents")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orgBody.replace("Blue Nile Trading PLC", "Blue Nile Trading PLC Updated")
                                .replace(
                                        "Blue Nile Trading PLC TIN",
                                        "Blue Nile Trading PLC Updated TIN")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/v1/search")
                        .param("tenantId", tenantId)
                        .param("q", "Nile")
                        .param("sourceTypes", "ORGANIZATION")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_search.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].document.sourceType").value("ORGANIZATION"))
                .andExpect(jsonPath("$[0].document.title").value("Blue Nile Trading PLC Updated"));
    }
}
