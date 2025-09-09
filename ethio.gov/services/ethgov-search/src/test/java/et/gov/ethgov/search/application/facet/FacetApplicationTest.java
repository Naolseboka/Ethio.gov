package et.gov.ethgov.search.application.facet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.search.adapter.out.persistence.InMemoryFacetRepository;
import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FacetApplicationTest {
    private InMemoryFacetRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFacetRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateFacetUseCase(repository, clock);
        var activate = new ActivateFacetUseCase(repository, clock);
        var get = new GetFacetUseCase(repository);

        var id = create.execute(new CreateFacetUseCase.CreateFacetCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateFacetUseCase.ActivateFacetCommand(
                null, null, null, null, id.toString(), null));

        Facet loaded = get.execute(id.toString());
        assertEquals(FacetStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
