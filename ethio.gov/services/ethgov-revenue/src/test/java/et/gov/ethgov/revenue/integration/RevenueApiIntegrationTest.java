package et.gov.ethgov.revenue.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.testing.containers.EthGovContainers;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
@Import(RevenueTestJwtConfig.class)
@Testcontainers
class RevenueApiIntegrationTest {

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
    void createsIssuesAndPaysBill() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "tenantId": "%s",
                  "type": "LICENSE_FEE",
                  "description": "Annual trade license fee",
                  "organizationId": "org_11111111-1111-4111-8111-111111111111",
                  "amountEtb": "1500.00",
                  "periodStart": "2024-01-01T00:00:00Z",
                  "periodEnd": "2024-12-31T00:00:00Z",
                  "licenseId": "lic_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/bills")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.billId").exists())
                .andReturn();

        String billId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.billId");

        mockMvc.perform(get("/v1/bills/" + billId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.billNumber").exists());

        mockMvc.perform(put("/v1/bills/" + billId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "description": "Annual trade license fee (adjusted)",
                                  "amountEtb": "1750.00",
                                  "periodStart": "2024-01-01T00:00:00Z",
                                  "periodEnd": "2024-12-31T00:00:00Z",
                                  "licenseId": "lic_11111111-1111-4111-8111-111111111111"
                                }
                                """))
                .andExpect(status().isNoContent());

        String dueAt = Instant.now().plus(30, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS).toString();
        mockMvc.perform(post("/v1/bills/" + billId + "/issue")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dueAt\":\"" + dueAt + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/v1/bills/" + billId + "/pay")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentReference\":\"pay_11111111-1111-4111-8111-111111111111\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/bills/" + billId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_revenue.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.amountEtb").value("1750.00"))
                .andExpect(jsonPath("$.paymentReference").exists());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.revenue.created.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
