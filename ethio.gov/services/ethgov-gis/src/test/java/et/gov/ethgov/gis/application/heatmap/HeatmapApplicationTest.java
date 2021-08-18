package et.gov.ethgov.gis.application.heatmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryHeatmapRepository;
import et.gov.ethgov.gis.domain.heatmap.Heatmap;
import et.gov.ethgov.gis.domain.heatmap.HeatmapStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeatmapApplicationTest {
    private InMemoryHeatmapRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryHeatmapRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateHeatmapUseCase(repository, clock);
        var activate = new ActivateHeatmapUseCase(repository, clock);
        var get = new GetHeatmapUseCase(repository);

        var id = create.execute(new CreateHeatmapUseCase.CreateHeatmapCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateHeatmapUseCase.ActivateHeatmapCommand(
                null, null, null, null, id.toString(), null));

        Heatmap loaded = get.execute(id.toString());
        assertEquals(HeatmapStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
