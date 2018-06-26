package et.gov.ethgov.reporting.integration;

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
@Import(ReportingTestJwtConfig.class)
@Testcontainers
class ReportingApiIntegrationTest {

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
    void registersPublishesRequestsAndSucceedsRun() throws Exception {
        String tenantId = TenantId.generate().value();
        String definitionBody =
                """
                {
                  "tenantId": "%s",
                  "code": "LICENSE_ISSUED_MONTHLY",
                  "name": "Licenses issued monthly",
                  "description": "Municipal license issuance summary",
                  "category": "LICENSE",
                  "format": "CSV",
                  "parameterSchema": "{\\"period\\":\\"month\\"}"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/report-definitions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(definitionBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportDefinitionId").exists())
                .andReturn();

        String definitionId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.reportDefinitionId");

        mockMvc.perform(post("/v1/report-definitions/" + definitionId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write"))))
                .andExpect(status().isNoContent());

        String runBody =
                """
                {
                  "tenantId": "%s",
                  "reportDefinitionId": "%s",
                  "parameters": "{\\"month\\":\\"2024-05\\"}",
                  "periodStart": "2024-05-01T00:00:00Z",
                  "periodEnd": "2024-05-31T23:59:59Z",
                  "requestedBy": "usr_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId, definitionId);

        MvcResult runCreated = mockMvc.perform(post("/v1/report-runs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(runBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportRunId").exists())
                .andReturn();

        String runId = com.jayway.jsonpath.JsonPath.read(
                runCreated.getResponse().getContentAsString(), "$.reportRunId");

        mockMvc.perform(post("/v1/report-runs/" + runId + "/start")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/report-runs/" + runId + "/succeed")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"artifactReference\":\"doc_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/report-runs/" + runId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCEEDED"))
                .andExpect(jsonPath("$.artifactReference")
                        .value("doc_11111111-1111-4111-8111-111111111111"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.reporting.definition.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }


    @Test
    void updatesCancelsDefinitionAndFailsRun() throws Exception {
        String tenantId = TenantId.generate().value();
        String definitionBody =
                """
                {
                  "tenantId": "%s",
                  "code": "REVENUE_OVERDUE_WEEKLY",
                  "name": "Overdue revenue weekly",
                  "category": "REVENUE",
                  "format": "PDF",
                  "parameterSchema": "{\\"week\\":\\"iso\\"}"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/report-definitions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(definitionBody))
                .andExpect(status().isCreated())
                .andReturn();
        String definitionId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.reportDefinitionId");

        String update =
                """
                {
                  "name": "Overdue revenue weekly v2",
                  "description": "Updated",
                  "category": "REVENUE",
                  "format": "XLSX",
                  "parameterSchema": "{\\"week\\":\\"iso\\",\\"region\\":\\"optional\\"}"
                }
                """;
        mockMvc.perform(put("/v1/report-definitions/" + definitionId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(update))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/report-definitions/lookup")
                        .param("tenantId", tenantId)
                        .param("code", "REVENUE_OVERDUE_WEEKLY")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.format").value("XLSX"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        // publish and create a run that fails
        mockMvc.perform(post("/v1/report-definitions/" + definitionId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write"))))
                .andExpect(status().isNoContent());

        String runBody =
                """
                {
                  "tenantId": "%s",
                  "reportDefinitionId": "%s",
                  "parameters": "{}"
                }
                """
                        .formatted(tenantId, definitionId);
        MvcResult runCreated = mockMvc.perform(post("/v1/report-runs")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(runBody))
                .andExpect(status().isCreated())
                .andReturn();
        String runId = com.jayway.jsonpath.JsonPath.read(
                runCreated.getResponse().getContentAsString(), "$.reportRunId");

        mockMvc.perform(post("/v1/report-runs/" + runId + "/fail")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"renderer unavailable\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/report-runs/" + runId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAILED"));

        // separate draft cancelled definition
        String cancelBody =
                """
                {
                  "tenantId": "%s",
                  "code": "GIS_FEATURES_DAILY",
                  "name": "GIS features daily",
                  "category": "GIS",
                  "format": "JSON",
                  "parameterSchema": "{}"
                }
                """
                        .formatted(tenantId);
        MvcResult cancelCreated = mockMvc.perform(post("/v1/report-definitions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cancelBody))
                .andExpect(status().isCreated())
                .andReturn();
        String cancelId = com.jayway.jsonpath.JsonPath.read(
                cancelCreated.getResponse().getContentAsString(), "$.reportDefinitionId");
        mockMvc.perform(post("/v1/report-definitions/" + cancelId + "/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"duplicate catalog entry\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/report-definitions/" + cancelId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_reporting.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
