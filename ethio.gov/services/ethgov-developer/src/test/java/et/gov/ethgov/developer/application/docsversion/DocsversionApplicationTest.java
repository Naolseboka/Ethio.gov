package et.gov.ethgov.developer.application.docsversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.developer.adapter.out.persistence.InMemoryDocsversionRepository;
import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocsversionApplicationTest {
    private InMemoryDocsversionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDocsversionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateDocsversionUseCase(repository, clock);
        var activate = new ActivateDocsversionUseCase(repository, clock);
        var get = new GetDocsversionUseCase(repository);

        var id = create.execute(new CreateDocsversionUseCase.CreateDocsversionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateDocsversionUseCase.ActivateDocsversionCommand(
                null, null, null, null, id.toString(), null));

        Docsversion loaded = get.execute(id.toString());
        assertEquals(DocsversionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
