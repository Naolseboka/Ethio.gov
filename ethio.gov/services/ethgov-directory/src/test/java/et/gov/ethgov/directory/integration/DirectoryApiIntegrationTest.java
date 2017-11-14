package et.gov.ethgov.directory.integration;

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
@Import(DirectoryTestJwtConfig.class)
@Testcontainers
class DirectoryApiIntegrationTest {

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
    void createsUnitRegistersEmployeeAndPublishesOutbox() throws Exception {
        String tenantId = TenantId.generate().value();
        String unitBody =
                """
                {
                  "tenantId": "%s",
                  "code": "FIN",
                  "name": "Finance Bureau",
                  "type": "BUREAU",
                  "description": "Municipal finance"
                }
                """
                        .formatted(tenantId);

        MvcResult unitCreated = mockMvc.perform(post("/v1/organizational-units")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_directory.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unitBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationalUnitId").exists())
                .andReturn();

        String unitId = com.jayway.jsonpath.JsonPath.read(
                unitCreated.getResponse().getContentAsString(), "$.organizationalUnitId");

        mockMvc.perform(get("/v1/organizational-units/" + unitId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_directory.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("FIN"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        String employeeBody =
                """
                {
                  "tenantId": "%s",
                  "employeeNumber": "EMP-100",
                  "citizenId": "cit_11111111-1111-4111-8111-111111111111",
                  "displayName": "Abebe Bekele",
                  "organizationalUnitId": "%s",
                  "jobTitle": "Budget Officer",
                  "hiredOn": "2020-01-15"
                }
                """
                        .formatted(tenantId, unitId);

        MvcResult empCreated = mockMvc.perform(post("/v1/public-employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_directory.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.publicEmployeeId").exists())
                .andReturn();

        String employeeId = com.jayway.jsonpath.JsonPath.read(
                empCreated.getResponse().getContentAsString(), "$.publicEmployeeId");

        mockMvc.perform(get("/v1/public-employees/" + employeeId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_directory.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeNumber").value("EMP-100"))
                .andExpect(jsonPath("$.jobTitle").value("Budget Officer"));

        mockMvc.perform(post("/v1/organizational-units/" + unitId + "/deactivate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_directory.write"))))
                .andExpect(status().isConflict());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.directory.unit-created.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
