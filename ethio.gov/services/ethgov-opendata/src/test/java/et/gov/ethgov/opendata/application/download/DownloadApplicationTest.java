package et.gov.ethgov.opendata.application.download;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryDownloadRepository;
import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DownloadApplicationTest {
    private InMemoryDownloadRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDownloadRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDownloadUseCase(repository, clock);
        var activate = new ActivateDownloadUseCase(repository, clock);
        var get = new GetDownloadUseCase(repository);

        var id = create.execute(new CreateDownloadUseCase.CreateDownloadCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDownloadUseCase.ActivateDownloadCommand(
                null, null, null, null, id.toString(), null));

        Download loaded = get.execute(id.toString());
        assertEquals(DownloadStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
