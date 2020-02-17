package et.gov.ethgov.developer.application.usage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryUsageRepository;
import et.gov.ethgov.developer.domain.usage.Usage;
import et.gov.ethgov.developer.domain.usage.UsageStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsageApplicationTest {
    private InMemoryUsageRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUsageRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateUsageUseCase(repository, clock);
        var activate = new ActivateUsageUseCase(repository, clock);
        var get = new GetUsageUseCase(repository);

        var id = create.execute(new CreateUsageUseCase.CreateUsageCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateUsageUseCase.ActivateUsageCommand(
                null, null, null, null, id.toString(), null));

        Usage loaded = get.execute(id.toString());
        assertEquals(UsageStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
