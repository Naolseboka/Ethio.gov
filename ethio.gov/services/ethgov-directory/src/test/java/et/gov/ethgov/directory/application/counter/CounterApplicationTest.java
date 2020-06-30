package et.gov.ethgov.directory.application.counter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryCounterRepository;
import et.gov.ethgov.directory.domain.counter.Counter;
import et.gov.ethgov.directory.domain.counter.CounterStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CounterApplicationTest {
    private InMemoryCounterRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCounterRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCounterUseCase(repository, clock);
        var activate = new ActivateCounterUseCase(repository, clock);
        var get = new GetCounterUseCase(repository);

        var id = create.execute(new CreateCounterUseCase.CreateCounterCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCounterUseCase.ActivateCounterCommand(
                null, null, null, null, id.toString(), null));

        Counter loaded = get.execute(id.toString());
        assertEquals(CounterStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
