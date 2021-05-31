package et.gov.ethgov.gis.application.overlay;

import et.gov.ethgov.gis.domain.overlay.Overlay;
import et.gov.ethgov.gis.domain.overlay.OverlayRepository;
import java.util.List;
import java.util.Objects;

public final class ListOverlayUseCase {
    private final OverlayRepository repository;

    public ListOverlayUseCase(OverlayRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Overlay> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
