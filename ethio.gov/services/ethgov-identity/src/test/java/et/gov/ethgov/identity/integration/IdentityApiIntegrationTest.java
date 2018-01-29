package et.gov.ethgov.identity.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.testing.containers.EthGovContainers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class IdentityApiIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = EthGovContainers.postgis();

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("ethgov.identity.bootstrap.enabled", () -> "false");
        registry.add("ethgov.identity.issuer", () -> "http://localhost:8081");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void registersCitizenWithoutAuth() throws Exception {
        mockMvc.perform(post("/v1/citizens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "username": "abebe@example.et",
                                  "password": "SecurePass1!xyz"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists());

        Integer outboxCount = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.identity.user.registered.v1");
        assertThat(outboxCount).isGreaterThanOrEqualTo(1);
    }

    @Test
    @WithMockUser(authorities = "SCOPE_identity.admin")
    void registersEmployeeAndWritesOutbox() throws Exception {
        String tenantId = TenantId.generate().value();
        mockMvc.perform(post("/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "username": "clerk@addis.gov.et",
                                  "password": "SecurePass1!xyz",
                                  "tenantId": "%s",
                                  "roles": ["CLERK"],
                                  "permissions": ["case:read"]
                                }
                                """
                                        .formatted(tenantId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").exists());
    }
}
