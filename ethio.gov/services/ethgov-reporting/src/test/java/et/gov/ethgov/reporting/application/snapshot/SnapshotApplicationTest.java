package et.gov.ethgov.reporting.application.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.reporting.adapter.out.persistence.InMemorySnapshotRepository;
import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
import et.gov.ethgov.reporting.domain.snapshot.SnapshotStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SnapshotApplicationTest {
    private InMemorySnapshotRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySnapshotRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSnapshotUseCase(repository, clock);
        var activate = new ActivateSnapshotUseCase(repository, clock);
        var get = new GetSnapshotUseCase(repository);

        var id = create.execute(new CreateSnapshotUseCase.CreateSnapshotCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSnapshotUseCase.ActivateSnapshotCommand(
                null, null, null, null, id.toString(), null));

        Snapshot loaded = get.execute(id.toString());
        assertEquals(SnapshotStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
