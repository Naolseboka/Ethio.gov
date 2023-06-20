package et.gov.ethgov.opendata.application.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.opendata.adapter.out.persistence.InMemoryCatalogRepository;
import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogApplicationTest {
    private InMemoryCatalogRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCatalogRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateCatalogUseCase(repository, clock);
        var activate = new ActivateCatalogUseCase(repository, clock);
        var get = new GetCatalogUseCase(repository);

        var id = create.execute(new CreateCatalogUseCase.CreateCatalogCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateCatalogUseCase.ActivateCatalogCommand(
                null, null, null, null, id.toString(), null));

        Catalog loaded = get.execute(id.toString());
        assertEquals(CatalogStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
