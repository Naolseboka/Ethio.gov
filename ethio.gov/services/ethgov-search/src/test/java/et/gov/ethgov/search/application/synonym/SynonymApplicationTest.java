package et.gov.ethgov.search.application.synonym;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemorySynonymRepository;
import et.gov.ethgov.search.domain.synonym.Synonym;
import et.gov.ethgov.search.domain.synonym.SynonymStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SynonymApplicationTest {
    private InMemorySynonymRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySynonymRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSynonymUseCase(repository, clock);
        var activate = new ActivateSynonymUseCase(repository, clock);
        var get = new GetSynonymUseCase(repository);

        var id = create.execute(new CreateSynonymUseCase.CreateSynonymCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSynonymUseCase.ActivateSynonymCommand(
                null, null, null, null, id.toString(), null));

        Synonym loaded = get.execute(id.toString());
        assertEquals(SynonymStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
