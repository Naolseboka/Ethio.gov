package et.gov.ethgov.payments.integration;

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
@Import(PaymentsTestJwtConfig.class)
@Testcontainers
class PaymentsApiIntegrationTest {

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
    void initiatesSucceedsAndRefundsPayment() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "billId": "rev_11111111-1111-4111-8111-111111111111",
                  "method": "MOBILE_MONEY",
                  "organizationId": "org_11111111-1111-4111-8111-111111111111",
                  "amountEtb": "1750.00",
                  "idempotencyKey": "idem-license-fee-001"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/payments")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_payments.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").exists())
                .andReturn();

        String paymentId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.paymentId");

        mockMvc.perform(get("/v1/payments/" + paymentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_payments.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INITIATED"))
                .andExpect(jsonPath("$.paymentNumber").exists())
                .andExpect(jsonPath("$.billId").exists());

        mockMvc.perform(post("/v1/payments/" + paymentId + "/succeed")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_payments.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerReference\":\"TELEBIRR-TX-9001\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/payments/" + paymentId + "/refund")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_payments.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"Customer requested refund\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/payments/" + paymentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_payments.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REFUNDED"))
                .andExpect(jsonPath("$.providerReference").value("TELEBIRR-TX-9001"));

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.payments.initiated.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
