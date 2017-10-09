package et.gov.ethgov.citizen.integration;

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
@Import(CitizenTestJwtConfig.class)
@Testcontainers
class CitizenApiIntegrationTest {

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
    void registersAndFetchesCitizen() throws Exception {
        String tenantId = TenantId.generate().value();
        String body =
                """
                {
                  "givenName": "Abebe",
                  "fatherName": "Bekele",
                  "grandfatherName": "Tesfaye",
                  "dateOfBirth": "1990-05-10",
                  "sex": "MALE",
                  "phone": "0911223344",
                  "registeringTenantId": "%s",
                  "regionCode": "AA",
                  "zoneCode": "AA-BOLE",
                  "zoneName": "Bole",
                  "woredaCode": "AA-BOLE-03",
                  "woredaName": "Woreda 03",
                  "kebeleCode": "AA-BOLE-03-05",
                  "kebeleName": "Kebele 05",
                  "street": "Africa Avenue",
                  "houseNumber": "12"
                }
                """
                        .formatted(tenantId);

        MvcResult created = mockMvc.perform(post("/v1/citizens")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_citizen.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.citizenId").exists())
                .andReturn();

        String citizenId = com.jayway.jsonpath.JsonPath.read(
                created.getResponse().getContentAsString(), "$.citizenId");

        mockMvc.perform(get("/v1/citizens/" + citizenId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_citizen.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Abebe Bekele Tesfaye"))
                .andExpect(jsonPath("$.phone").value("+251911223344"))
                .andExpect(jsonPath("$.dateOfBirthEthiopian").exists());

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.citizen.registered.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }
}
