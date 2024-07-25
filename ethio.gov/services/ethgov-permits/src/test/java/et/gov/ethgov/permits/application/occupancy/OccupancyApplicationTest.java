package et.gov.ethgov.permits.application.occupancy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.permits.adapter.out.persistence.InMemoryOccupancyRepository;
import et.gov.ethgov.permits.domain.occupancy.Occupancy;
import et.gov.ethgov.permits.domain.occupancy.OccupancyStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OccupancyApplicationTest {
    private InMemoryOccupancyRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOccupancyRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOccupancyUseCase(repository, clock);
        var activate = new ActivateOccupancyUseCase(repository, clock);
        var get = new GetOccupancyUseCase(repository);

        var id = create.execute(new CreateOccupancyUseCase.CreateOccupancyCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOccupancyUseCase.ActivateOccupancyCommand(
                null, null, null, null, id.toString(), null));

        Occupancy loaded = get.execute(id.toString());
        assertEquals(OccupancyStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
