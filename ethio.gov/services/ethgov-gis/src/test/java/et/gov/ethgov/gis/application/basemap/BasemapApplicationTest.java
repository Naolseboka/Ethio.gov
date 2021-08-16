package et.gov.ethgov.gis.application.basemap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryBasemapRepository;
import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BasemapApplicationTest {
    private InMemoryBasemapRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBasemapRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateBasemapUseCase(repository, clock);
        var activate = new ActivateBasemapUseCase(repository, clock);
        var get = new GetBasemapUseCase(repository);

        var id = create.execute(new CreateBasemapUseCase.CreateBasemapCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateBasemapUseCase.ActivateBasemapCommand(
                null, null, null, null, id.toString(), null));

        Basemap loaded = get.execute(id.toString());
        assertEquals(BasemapStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
