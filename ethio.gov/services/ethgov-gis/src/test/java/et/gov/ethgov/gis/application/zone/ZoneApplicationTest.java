package et.gov.ethgov.gis.application.zone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryZoneRepository;
import et.gov.ethgov.gis.domain.zone.Zone;
import et.gov.ethgov.gis.domain.zone.ZoneStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoneApplicationTest {
    private InMemoryZoneRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryZoneRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateZoneUseCase(repository, clock);
        var activate = new ActivateZoneUseCase(repository, clock);
        var get = new GetZoneUseCase(repository);

        var id = create.execute(new CreateZoneUseCase.CreateZoneCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateZoneUseCase.ActivateZoneCommand(
                null, null, null, null, id.toString(), null));

        Zone loaded = get.execute(id.toString());
        assertEquals(ZoneStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
