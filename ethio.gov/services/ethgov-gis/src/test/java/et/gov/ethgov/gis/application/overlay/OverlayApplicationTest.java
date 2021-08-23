package et.gov.ethgov.gis.application.overlay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import et.gov.ethgov.gis.adapter.out.persistence.InMemoryOverlayRepository;
import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OverlayApplicationTest {
    private InMemoryOverlayRepository repository;
    private Clock clock;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOverlayRepository();
        clock = Clock.fixed(Instant.parse("2024-06-01T08:00:00Z"), ZoneOffset.UTC);
    }

    @Test
    void createAndActivateFlow() {
        var create = new CreateOverlayUseCase(repository, clock);
        var activate = new ActivateOverlayUseCase(repository, clock);
        var get = new GetOverlayUseCase(repository);

        var id = create.execute(new CreateOverlayUseCase.CreateOverlayCommand(
                "APP-100", "Title", "Body", "tenant-x", null, null));
        assertNotNull(id);

        activate.execute(new ActivateOverlayUseCase.ActivateOverlayCommand(
                null, null, null, null, id.toString(), null));

        Overlay loaded = get.execute(id.toString());
        assertEquals(OverlayStatus.ACTIVE, loaded.status());
        assertEquals("APP-100", loaded.referenceCode());
    }
}
