package et.gov.ethgov.directory.application.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryQueueRepository;
import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QueueApplicationTest {
    private InMemoryQueueRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQueueRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateQueueUseCase(repository, clock);
        var activate = new ActivateQueueUseCase(repository, clock);
        var get = new GetQueueUseCase(repository);

        var id = create.execute(new CreateQueueUseCase.CreateQueueCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateQueueUseCase.ActivateQueueCommand(
                null, null, null, null, id.toString(), null));

        Queue loaded = get.execute(id.toString());
        assertEquals(QueueStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
