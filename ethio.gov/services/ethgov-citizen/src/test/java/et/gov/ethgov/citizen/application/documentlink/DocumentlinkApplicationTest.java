package et.gov.ethgov.citizen.application.documentlink;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.citizen.adapter.out.persistence.InMemoryDocumentlinkRepository;
import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentlinkApplicationTest {
    private InMemoryDocumentlinkRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentlinkRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDocumentlinkUseCase(repository, clock);
        var activate = new ActivateDocumentlinkUseCase(repository, clock);
        var get = new GetDocumentlinkUseCase(repository);

        var id = create.execute(new CreateDocumentlinkUseCase.CreateDocumentlinkCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDocumentlinkUseCase.ActivateDocumentlinkCommand(
                null, null, null, null, id.toString(), null));

        Documentlink loaded = get.execute(id.toString());
        assertEquals(DocumentlinkStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
