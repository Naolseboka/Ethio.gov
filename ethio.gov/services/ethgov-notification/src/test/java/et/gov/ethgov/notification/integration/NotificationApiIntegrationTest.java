package et.gov.ethgov.notification.integration;

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
@Import(NotificationTestJwtConfig.class)
@Testcontainers
class NotificationApiIntegrationTest {

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
    void schedulesSendsAndDeliversSmsNotification() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "channel": "SMS",
                  "phone": "+251911234567",
                  "locale": "am",
                  "body": "Your business license was issued",
                  "templateCode": "LICENSE_ISSUED",
                  "citizenId": "cit_11111111-1111-4111-8111-111111111111",
                  "sourceReference": "pay_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/notifications")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notification.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.notificationId").exists())
                .andReturn();

        String notificationId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.notificationId");

        mockMvc.perform(get("/v1/notifications/" + notificationId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notification.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.channel").value("SMS"))
                .andExpect(jsonPath("$.phone").value("+251911234567"));

        mockMvc.perform(post("/v1/notifications/" + notificationId + "/sent")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notification.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerReference\":\"SMS-GW-1001\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/notifications/" + notificationId + "/delivered")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notification.write"))))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/notifications/" + notificationId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_notification.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.providerReference").value("SMS-GW-1001"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.notification.scheduled.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
