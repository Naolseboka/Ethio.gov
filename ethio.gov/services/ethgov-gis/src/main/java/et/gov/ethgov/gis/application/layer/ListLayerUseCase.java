package et.gov.ethgov.gis.application.layer;

import et.gov.ethgov.gis.domain.layer.Layer;
import et.gov.ethgov.gis.domain.layer.LayerRepository;
import java.util.List;
import java.util.Objects;

public final class ListLayerUseCase {
    private final LayerRepository repository;

    public ListLayerUseCase(LayerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Layer> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
