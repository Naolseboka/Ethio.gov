package et.gov.ethgov.gis.application.gisexport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryGisexportRepository;
import et.gov.ethgov.gis.domain.gisexport.Gisexport;
import et.gov.ethgov.gis.domain.gisexport.GisexportStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GisexportApplicationTest {
    private InMemoryGisexportRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGisexportRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateGisexportUseCase(repository, clock);
        var activate = new ActivateGisexportUseCase(repository, clock);
        var get = new GetGisexportUseCase(repository);

        var id = create.execute(new CreateGisexportUseCase.CreateGisexportCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateGisexportUseCase.ActivateGisexportCommand(
                null, null, null, null, id.toString(), null));

        Gisexport loaded = get.execute(id.toString());
        assertEquals(GisexportStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
