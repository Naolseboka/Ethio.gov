package et.gov.ethgov.document.application.documentset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.document.adapter.out.persistence.InMemoryDocumentsetRepository;
import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocumentsetApplicationTest {
    private InMemoryDocumentsetRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocumentsetRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDocumentsetUseCase(repository, clock);
        var activate = new ActivateDocumentsetUseCase(repository, clock);
        var get = new GetDocumentsetUseCase(repository);

        var id = create.execute(new CreateDocumentsetUseCase.CreateDocumentsetCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDocumentsetUseCase.ActivateDocumentsetCommand(
                null, null, null, null, id.toString(), null));

        Documentset loaded = get.execute(id.toString());
        assertEquals(DocumentsetStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
