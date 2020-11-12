package et.gov.ethgov.document.application.ocrjob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryOcrjobRepository;
import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OcrjobApplicationTest {
    private InMemoryOcrjobRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOcrjobRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOcrjobUseCase(repository, clock);
        var activate = new ActivateOcrjobUseCase(repository, clock);
        var get = new GetOcrjobUseCase(repository);

        var id = create.execute(new CreateOcrjobUseCase.CreateOcrjobCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOcrjobUseCase.ActivateOcrjobCommand(
                null, null, null, null, id.toString(), null));

        Ocrjob loaded = get.execute(id.toString());
        assertEquals(OcrjobStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
