package et.gov.ethgov.search.application.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryIndexRepository;
import et.gov.ethgov.search.domain.index.Index;
import et.gov.ethgov.search.domain.index.IndexStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexApplicationTest {
    private InMemoryIndexRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryIndexRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateIndexUseCase(repository, clock);
        var activate = new ActivateIndexUseCase(repository, clock);
        var get = new GetIndexUseCase(repository);

        var id = create.execute(new CreateIndexUseCase.CreateIndexCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateIndexUseCase.ActivateIndexCommand(
                null, null, null, null, id.toString(), null));

        Index loaded = get.execute(id.toString());
        assertEquals(IndexStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
