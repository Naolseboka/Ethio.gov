package et.gov.ethgov.gis.application.overlay;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayId;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import java.util.Objects;

public final class GetOverlayUseCase {
    private final OverlayRepository repository;

    public GetOverlayUseCase(OverlayRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Overlay execute(String rawId) {
        OverlayId id = OverlayId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("overlay not found: " + rawId));
    }
}
