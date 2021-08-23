package et.gov.ethgov.gis.application.spatialquery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemorySpatialqueryRepository;
import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpatialqueryApplicationTest {
    private InMemorySpatialqueryRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemorySpatialqueryRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateSpatialqueryUseCase(repository, clock);
        var activate = new ActivateSpatialqueryUseCase(repository, clock);
        var get = new GetSpatialqueryUseCase(repository);

        var id = create.execute(new CreateSpatialqueryUseCase.CreateSpatialqueryCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateSpatialqueryUseCase.ActivateSpatialqueryCommand(
                null, null, null, null, id.toString(), null));

        Spatialquery loaded = get.execute(id.toString());
        assertEquals(SpatialqueryStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
