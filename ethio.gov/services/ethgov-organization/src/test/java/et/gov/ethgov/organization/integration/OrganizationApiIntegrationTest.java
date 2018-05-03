package et.gov.ethgov.organization.integration;

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
@Import(OrganizationTestJwtConfig.class)
@Testcontainers
class OrganizationApiIntegrationTest {

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
    void registersAssignsTinAndFetchesOrganization() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "legalName": "Ethio Logistics PLC",
                  "tradeName": "Ethio Logistics",
                  "type": "PRIVATE_COMPANY",
                  "phone": "0911223344",
                  "email": "info@ethiologistics.et",
                  "registeringTenantId": "%s",
                  "establishedOn": "2015-03-15",
                  "regionCode": "AA",
                  "zoneCode": "AA-BOLE",
                  "zoneName": "Bole",
                  "woredaCode": "AA-BOLE-03",
                  "woredaName": "Woreda 03",
                  "kebeleCode": "AA-BOLE-03-05",
                  "kebeleName": "Kebele 05",
                  "street": "Africa Avenue",
                  "houseNumber": "100"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/organizations")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_organization.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationId").exists())
                .andReturn();

        String organizationId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.organizationId");

        mockMvc.perform(post("/v1/organizations/" + organizationId + "/tin")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_organization.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tin\":\"0012345678\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/organizations/" + organizationId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_organization.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalName").value("Ethio Logistics PLC"))
                .andExpect(jsonPath("$.tin").value("0012345678"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(get("/v1/organizations/search")
                        .param("tin", "0012345678")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_organization.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.organizationId").value(organizationId));

        mockMvc.perform(post("/v1/organizations/" + organizationId + "/suspend")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_organization.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\":\"License suspension pending review\"}"))
                .andExpect(status().isNoContent());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.organization.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
