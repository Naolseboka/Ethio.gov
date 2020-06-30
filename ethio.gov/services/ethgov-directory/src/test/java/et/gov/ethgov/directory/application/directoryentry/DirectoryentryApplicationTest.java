package et.gov.ethgov.directory.application.directoryentry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.directory.adapter.out.persistence.InMemoryDirectoryentryRepository;
import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectoryentryApplicationTest {
    private InMemoryDirectoryentryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDirectoryentryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDirectoryentryUseCase(repository, clock);
        var activate = new ActivateDirectoryentryUseCase(repository, clock);
        var get = new GetDirectoryentryUseCase(repository);

        var id = create.execute(new CreateDirectoryentryUseCase.CreateDirectoryentryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDirectoryentryUseCase.ActivateDirectoryentryCommand(
                null, null, null, null, id.toString(), null));

        Directoryentry loaded = get.execute(id.toString());
        assertEquals(DirectoryentryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
