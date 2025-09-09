package et.gov.ethgov.search.application.searchdocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemorySearchdocumentRepository;
import et.gov.ethgov.search.domain.searchdocument.Searchdocument;
import et.gov.ethgov.search.domain.searchdocument.SearchdocumentStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SearchdocumentApplicationTest {
    private InMemorySearchdocumentRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySearchdocumentRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSearchdocumentUseCase(repository, clock);
        var activate = new ActivateSearchdocumentUseCase(repository, clock);
        var get = new GetSearchdocumentUseCase(repository);

        var id = create.execute(new CreateSearchdocumentUseCase.CreateSearchdocumentCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSearchdocumentUseCase.ActivateSearchdocumentCommand(
                null, null, null, null, id.toString(), null));

        Searchdocument loaded = get.execute(id.toString());
        assertEquals(SearchdocumentStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
