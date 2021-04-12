package et.gov.ethgov.gateway.application.healthcheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryHealthcheckRepository;
import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
import et.gov.ethgov.gateway.domain.healthcheck.HealthcheckStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HealthcheckApplicationTest {
    private InMemoryHealthcheckRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHealthcheckRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHealthcheckUseCase(repository, clock);
        var activate = new ActivateHealthcheckUseCase(repository, clock);
        var get = new GetHealthcheckUseCase(repository);

        var id = create.execute(new CreateHealthcheckUseCase.CreateHealthcheckCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHealthcheckUseCase.ActivateHealthcheckCommand(
                null, null, null, null, id.toString(), null));

        Healthcheck loaded = get.execute(id.toString());
        assertEquals(HealthcheckStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
