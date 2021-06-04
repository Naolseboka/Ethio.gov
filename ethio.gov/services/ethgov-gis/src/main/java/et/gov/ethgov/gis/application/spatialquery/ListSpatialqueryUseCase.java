package et.gov.ethgov.gis.application.spatialquery;

import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
import et.gov.ethgov.gis.domain.spatialquery.SpatialqueryRepository;
import java.util.List;
import java.util.Objects;

public final class ListSpatialqueryUseCase {
    private final SpatialqueryRepository repository;

    public ListSpatialqueryUseCase(SpatialqueryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Spatialquery> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
