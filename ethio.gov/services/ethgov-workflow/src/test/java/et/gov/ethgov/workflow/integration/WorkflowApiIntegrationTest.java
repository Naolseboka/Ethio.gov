package et.gov.ethgov.workflow.integration;

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
@Import(WorkflowTestJwtConfig.class)
@Testcontainers
class WorkflowApiIntegrationTest {

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
    void createsPublishesStartsAndCompletesTwoStepProcess() throws Exception {
        String tenantId = TenantId.generate().value();
        String user = "usr_11111111-1111-4111-8111-111111111111";

        String defBody =
                """
                {
                  "tenantId": "%s",
                  "key": "business-license",
                  "name": "Business License",
                  "description": "Municipal licensing",
                  "steps": [
                    {"stepKey": "intake", "name": "Intake Review", "type": "USER_TASK", "assigneeRole": "clerk"},
                    {"stepKey": "approve", "name": "Manager Approval", "type": "USER_TASK", "assigneeRole": "manager"}
                  ]
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/workflow-definitions")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(defBody))
                .andExpect(status().isCreated())
                .andReturn();

        String definitionId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.definitionId");

        mockMvc.perform(post("/v1/workflow-definitions/" + definitionId + "/publish")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write"))))
                .andExpect(status().isNoContent());

        String startBody =
                """
                {
                  "tenantId": "%s",
                  "definitionKey": "business-license",
                  "businessKey": "BL-2024-001",
                  "startedByAccountId": "%s"
                }
                """
                        .formatted(tenantId, user);

        MvcResult started = mockMvc.perform(post("/v1/process-instances")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startBody))
                .andExpect(status().isCreated())
                .andReturn();

        String processId = com.jayway.jsonpath.JsonPath.read(
                started.getResponse().getContentAsString(), "$.processInstanceId");

        String taskId = jdbcTemplate.queryForObject(
                "select id from wf_task where process_instance_id = ? and status = 'OPEN'",
                String.class,
                processId);

        mockMvc.perform(post("/v1/workflow-tasks/" + taskId + "/claim")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + user + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/workflow-tasks/" + taskId + "/complete")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"outcome\":\"APPROVED\",\"accountId\":\"" + user + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/process-instances/" + processId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStepKey").value("approve"))
                .andExpect(jsonPath("$.status").value("RUNNING"));

        String task2 = jdbcTemplate.queryForObject(
                "select id from wf_task where process_instance_id = ? and status = 'OPEN'",
                String.class,
                processId);

        mockMvc.perform(post("/v1/workflow-tasks/" + task2 + "/claim")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\":\"" + user + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/workflow-tasks/" + task2 + "/complete")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"outcome\":\"APPROVED\",\"accountId\":\"" + user + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/process-instances/" + processId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_workflow.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.workflow.process-started.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
