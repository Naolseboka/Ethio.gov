package et.gov.ethgov.document.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.testing.containers.EthGovContainers;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(DocumentTestJwtConfig.class)
@Testcontainers
class DocumentApiIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = EthGovContainers.postgis();

    @Container
    static final GenericContainer<?> MINIO = new GenericContainer<>(
                    DockerImageName.parse("minio/minio:RELEASE.2024-12-18T13-15-44Z"))
            .withEnv("MINIO_ROOT_USER", "minio")
            .withEnv("MINIO_ROOT_PASSWORD", "minio12345")
            .withCommand("server", "/data")
            .withExposedPorts(9000)
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("ethgov.identity.issuer", () -> "http://localhost:8081");
        registry.add(
                "ethgov.document.storage.endpoint",
                () -> "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(9000));
        registry.add("ethgov.document.storage.region", () -> "us-east-1");
        registry.add("ethgov.document.storage.access-key", () -> "minio");
        registry.add("ethgov.document.storage.secret-key", () -> "minio12345");
        registry.add("ethgov.document.storage.bucket", () -> "ethgov-documents");
        registry.add("ethgov.document.storage.path-style-access", () -> "true");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void initiatesUploadCompletesAndDownloads() throws Exception {
        String tenantId = TenantId.generate().value();
        byte[] payload = "ethgov-document-bytes".getBytes(StandardCharsets.UTF_8);
        String hash = sha256(payload);

        String initiateBody =
                """
                {
                  "tenantId": "%s",
                  "title": "Land Title Scan",
                  "description": "Kebele copy",
                  "classification": "CONFIDENTIAL",
                  "contentType": "application/pdf",
                  "fileName": "title.pdf",
                  "expectedSizeBytes": %d,
                  "ownerType": "CITIZEN",
                  "ownerId": "cit_11111111-1111-4111-8111-111111111111",
                  "uploadedByAccountId": "usr_11111111-1111-4111-8111-111111111111"
                }
                """
                        .formatted(tenantId, payload.length);

        MvcResult initiated = mockMvc.perform(post("/v1/documents/uploads")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_document.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initiateBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentId").exists())
                .andExpect(jsonPath("$.uploadUrl").exists())
                .andReturn();

        String documentId = com.jayway.jsonpath.JsonPath.read(
                initiated.getResponse().getContentAsString(), "$.documentId");
        String uploadUrl = com.jayway.jsonpath.JsonPath.read(
                initiated.getResponse().getContentAsString(), "$.uploadUrl");

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> putResponse = httpClient.send(
                HttpRequest.newBuilder(URI.create(uploadUrl))
                        .header("Content-Type", "application/pdf")
                        .PUT(HttpRequest.BodyPublishers.ofByteArray(payload))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(putResponse.statusCode()).isBetween(200, 299);

        mockMvc.perform(post("/v1/documents/" + documentId + "/complete")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_document.write")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contentHash\":\"" + hash + "\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/documents/" + documentId)
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_document.read"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.contentHash").value(hash));

        MvcResult download = mockMvc.perform(post("/v1/documents/" + documentId + "/download-url")
                        .with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_document.write"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.downloadUrl").exists())
                .andReturn();

        String downloadUrl = com.jayway.jsonpath.JsonPath.read(
                download.getResponse().getContentAsString(), "$.downloadUrl");
        HttpResponse<byte[]> getResponse = httpClient.send(
                HttpRequest.newBuilder(URI.create(downloadUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofByteArray());
        assertThat(getResponse.statusCode()).isBetween(200, 299);
        assertThat(getResponse.body()).isEqualTo(payload);

        Integer outbox = jdbcTemplate.queryForObject(
                "select count(*) from platform_outbox where event_type = ?",
                Integer.class,
                "et.gov.ethgov.document.draft-created.v1");
        assertThat(outbox).isGreaterThanOrEqualTo(1);
    }

    private static String sha256(byte[] bytes) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
        return HexFormat.of().formatHex(digest);
    }
}
