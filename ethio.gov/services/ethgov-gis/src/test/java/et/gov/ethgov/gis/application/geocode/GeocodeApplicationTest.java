package et.gov.ethgov.gis.application.geocode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryGeocodeRepository;
import et.gov.ethgov.gis.domain.geocode.Geocode;
import et.gov.ethgov.gis.domain.geocode.GeocodeStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeocodeApplicationTest {
    private InMemoryGeocodeRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGeocodeRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateGeocodeUseCase(repository, clock);
        var activate = new ActivateGeocodeUseCase(repository, clock);
        var get = new GetGeocodeUseCase(repository);

        var id = create.execute(new CreateGeocodeUseCase.CreateGeocodeCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateGeocodeUseCase.ActivateGeocodeCommand(
                null, null, null, null, id.toString(), null));

        Geocode loaded = get.execute(id.toString());
        assertEquals(GeocodeStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
