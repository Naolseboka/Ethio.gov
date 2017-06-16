package et.gov.ethgov.platform.testing.containers;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Canonical Testcontainers images for EthGov integration tests.
 */
public final class EthGovContainers {

    public static final DockerImageName POSTGIS =
            DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                    .asCompatibleSubstituteFor("postgres");
    public static final DockerImageName KAFKA =
            DockerImageName.parse("apache/kafka-native:3.8.1");
    public static final DockerImageName REDIS =
            DockerImageName.parse("redis:7.4-alpine");

    private EthGovContainers() {}

    @SuppressWarnings("resource")
    public static PostgreSQLContainer<?> postgis() {
        return new PostgreSQLContainer<>(POSTGIS)
                .withDatabaseName("ethgov")
                .withUsername("ethgov")
                .withPassword("ethgov");
    }

    @SuppressWarnings("resource")
    public static KafkaContainer kafka() {
        return new KafkaContainer(KAFKA);
    }
}
