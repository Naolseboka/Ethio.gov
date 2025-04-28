package et.gov.ethgov.revenue.application.collector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.revenue.adapter.out.persistence.InMemoryCollectorRepository;
import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectorApplicationTest {
    private InMemoryCollectorRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCollectorRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCollectorUseCase(repository, clock);
        var activate = new ActivateCollectorUseCase(repository, clock);
        var get = new GetCollectorUseCase(repository);

        var id = create.execute(new CreateCollectorUseCase.CreateCollectorCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCollectorUseCase.ActivateCollectorCommand(
                null, null, null, null, id.toString(), null));

        Collector loaded = get.execute(id.toString());
        assertEquals(CollectorStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
