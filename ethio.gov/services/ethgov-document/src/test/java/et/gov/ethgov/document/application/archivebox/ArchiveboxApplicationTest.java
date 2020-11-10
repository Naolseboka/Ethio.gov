package et.gov.ethgov.document.application.archivebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryArchiveboxRepository;
import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ArchiveboxApplicationTest {
    private InMemoryArchiveboxRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArchiveboxRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateArchiveboxUseCase(repository, clock);
        var activate = new ActivateArchiveboxUseCase(repository, clock);
        var get = new GetArchiveboxUseCase(repository);

        var id = create.execute(new CreateArchiveboxUseCase.CreateArchiveboxCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateArchiveboxUseCase.ActivateArchiveboxCommand(
                null, null, null, null, id.toString(), null));

        Archivebox loaded = get.execute(id.toString());
        assertEquals(ArchiveboxStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
