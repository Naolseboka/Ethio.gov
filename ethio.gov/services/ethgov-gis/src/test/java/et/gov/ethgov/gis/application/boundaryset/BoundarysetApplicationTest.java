package et.gov.ethgov.gis.application.boundaryset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryBoundarysetRepository;
import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
import et.gov.ethgov.gis.domain.boundaryset.BoundarysetStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundarysetApplicationTest {
    private InMemoryBoundarysetRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBoundarysetRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBoundarysetUseCase(repository, clock);
        var activate = new ActivateBoundarysetUseCase(repository, clock);
        var get = new GetBoundarysetUseCase(repository);

        var id = create.execute(new CreateBoundarysetUseCase.CreateBoundarysetCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBoundarysetUseCase.ActivateBoundarysetCommand(
                null, null, null, null, id.toString(), null));

        Boundaryset loaded = get.execute(id.toString());
        assertEquals(BoundarysetStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
