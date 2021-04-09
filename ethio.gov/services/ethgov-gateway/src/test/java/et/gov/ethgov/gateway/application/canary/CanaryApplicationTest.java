package et.gov.ethgov.gateway.application.canary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gateway.adapter.out.persistence.InMemoryCanaryRepository;
import et.gov.ethgov.gateway.domain.canary.Canary;
import et.gov.ethgov.gateway.domain.canary.CanaryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CanaryApplicationTest {
    private InMemoryCanaryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCanaryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCanaryUseCase(repository, clock);
        var activate = new ActivateCanaryUseCase(repository, clock);
        var get = new GetCanaryUseCase(repository);

        var id = create.execute(new CreateCanaryUseCase.CreateCanaryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCanaryUseCase.ActivateCanaryCommand(
                null, null, null, null, id.toString(), null));

        Canary loaded = get.execute(id.toString());
        assertEquals(CanaryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
