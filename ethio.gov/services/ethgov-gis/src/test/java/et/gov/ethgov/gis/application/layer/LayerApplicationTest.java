package et.gov.ethgov.gis.application.layer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryLayerRepository;
import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LayerApplicationTest {
    private InMemoryLayerRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryLayerRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateLayerUseCase(repository, clock);
        var activate = new ActivateLayerUseCase(repository, clock);
        var get = new GetLayerUseCase(repository);

        var id = create.execute(new CreateLayerUseCase.CreateLayerCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateLayerUseCase.ActivateLayerCommand(
                null, null, null, null, id.toString(), null));

        Layer loaded = get.execute(id.toString());
        assertEquals(LayerStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
