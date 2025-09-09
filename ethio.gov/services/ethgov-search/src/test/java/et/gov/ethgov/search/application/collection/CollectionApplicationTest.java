package et.gov.ethgov.search.application.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryCollectionRepository;
import et.gov.ethgov.search.domain.collection.Collection;
import et.gov.ethgov.search.domain.collection.CollectionStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectionApplicationTest {
    private InMemoryCollectionRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCollectionRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCollectionUseCase(repository, clock);
        var activate = new ActivateCollectionUseCase(repository, clock);
        var get = new GetCollectionUseCase(repository);

        var id = create.execute(new CreateCollectionUseCase.CreateCollectionCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCollectionUseCase.ActivateCollectionCommand(
                null, null, null, null, id.toString(), null));

        Collection loaded = get.execute(id.toString());
        assertEquals(CollectionStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
