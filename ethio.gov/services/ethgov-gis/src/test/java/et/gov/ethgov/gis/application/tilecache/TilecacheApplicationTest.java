package et.gov.ethgov.gis.application.tilecache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryTilecacheRepository;
import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TilecacheApplicationTest {
    private InMemoryTilecacheRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTilecacheRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateTilecacheUseCase(repository, clock);
        var activate = new ActivateTilecacheUseCase(repository, clock);
        var get = new GetTilecacheUseCase(repository);

        var id = create.execute(new CreateTilecacheUseCase.CreateTilecacheCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateTilecacheUseCase.ActivateTilecacheCommand(
                null, null, null, null, id.toString(), null));

        Tilecache loaded = get.execute(id.toString());
        assertEquals(TilecacheStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
