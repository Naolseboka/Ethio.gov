package et.gov.ethgov.developer.integration;

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
@Import(DeveloperTestJwtConfig.class)
@Testcontainers
class DeveloperApiIntegrationTest {

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
    void registersActivatesIssuesAndRevokesCredential() throws Exception {
        String tenantId = TenantId.generate().value();
        String appBody =
                """
                {
                  "tenantId": "%s",
                  "slug": "bole-partner-portal",
                  "name": "Bole Partner Portal",
                  "description": "Municipal partner integration",
                  "ownerOrgId": "org_11111111-1111-4111-8111-111111111111",
                  "contactEmail": "dev@partner.et",
                  "allowedScopes": "[\\"citizen.read\\",\\"opendata.read\\"]"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/apps")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.developerAppId").exists())
                .andReturn();

        String appId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.developerAppId");

        mockMvc.perform(put("/v1/apps/" + appId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "name": "Bole Partner Portal v2",
                                  "description": "Updated partner integration",
                                  "ownerOrgId": "org_11111111-1111-4111-8111-111111111111",
                                  "contactEmail": "dev@partner.et",
                                  "allowedScopes": "[\\"citizen.read\\",\\"opendata.read\\",\\"analytics.read\\"]"
                                }
                                """))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/apps/" + appId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write"))))
                .andExpect(status().isNoContent());

        String credBody =
                """
                {
                  "tenantId": "%s",
                  "developerAppId": "%s",
                  "label": "production key"
                }
                """
                        .formatted(tenantId, appId);

        MvcResult issued = mockMvc.perform(post("/v1/credentials")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.apiCredentialId").exists())
                .andExpect(jsonPath("$.apiKey").exists())
                .andExpect(jsonPath("$.keyPrefix").exists())
                .andReturn();

        String credentialId = com.jayway.jsonpath.JsonPath.read(
                issued.getResponse().getContentAsString(), "$.apiCredentialId");
        String apiKey = com.jayway.jsonpath.JsonPath.read(
                issued.getResponse().getContentAsString(), "$.apiKey");
        assertThat(apiKey).startsWith("egk_");

        mockMvc.perform(get("/v1/credentials/by-app/" + appId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].apiCredentialId").value(credentialId))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        // secret must not be stored in plaintext
        Integer plaintext = jdbcTemplate.queryForObject(
                "select count(*) from dev_credential where key_hash = ?", Integer.class, apiKey);
        assertThat(plaintext).isZero();

        mockMvc.perform(post("/v1/credentials/" + credentialId + "/revoke")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"rotated after incident\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/apps/lookup")
                        .param("tenantId", tenantId)
                        .param("slug", "bole-partner-portal")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.developer.app.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }

    @Test
    void cancelsDraftApp() throws Exception {
        String tenantId = TenantId.generate().value();
        String appBody =
                """
                {
                  "tenantId": "%s",
                  "slug": "draft-only-app",
                  "name": "Draft Only App",
                  "contactEmail": "draft@partner.et",
                  "allowedScopes": "[\\"opendata.read\\"]"
                }
                """
                        .formatted(tenantId);
        MvcResult created = mockMvc.perform(post("/v1/apps")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appBody))
                .andExpect(status().isCreated())
                .andReturn();
        String appId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.developerAppId");
        mockMvc.perform(post("/v1/apps/" + appId + "/cancel")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"abandoned registration\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/v1/apps/" + appId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }


    @Test
    void suspendsAndRetiresActiveApp() throws Exception {
        String tenantId = TenantId.generate().value();
        String appBody =
                """
                {
                  "tenantId": "%s",
                  "slug": "suspend-retire-app",
                  "name": "Suspend Retire App",
                  "contactEmail": "ops@partner.et",
                  "allowedScopes": "[\\"analytics.read\\"]"
                }
                """
                        .formatted(tenantId);
        MvcResult created = mockMvc.perform(post("/v1/apps")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appBody))
                .andExpect(status().isCreated())
                .andReturn();
        String appId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.developerAppId");

        mockMvc.perform(post("/v1/apps/" + appId + "/activate")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/apps/" + appId + "/suspend")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"temporary compliance hold\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/apps/" + appId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUSPENDED"));

        mockMvc.perform(post("/v1/apps/" + appId + "/retire")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/apps/" + appId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_developer.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETIRED"));
    }
}
