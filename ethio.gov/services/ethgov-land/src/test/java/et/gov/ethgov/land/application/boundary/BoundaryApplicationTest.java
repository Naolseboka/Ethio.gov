package et.gov.ethgov.land.application.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.land.adapter.out.persistence.InMemoryBoundaryRepository;
import et.gov.ethgov.land.domain.boundary.Boundary;
import et.gov.ethgov.land.domain.boundary.BoundaryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundaryApplicationTest {
    private InMemoryBoundaryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBoundaryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBoundaryUseCase(repository, clock);
        var activate = new ActivateBoundaryUseCase(repository, clock);
        var get = new GetBoundaryUseCase(repository);

        var id = create.execute(new CreateBoundaryUseCase.CreateBoundaryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBoundaryUseCase.ActivateBoundaryCommand(
                null, null, null, null, id.toString(), null));

        Boundary loaded = get.execute(id.toString());
        assertEquals(BoundaryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
