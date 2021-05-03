package et.gov.ethgov.gis.application.basemap;

import et.gov.ethgov.gis.domain.basemap.Basemap;
import et.gov.ethgov.gis.domain.basemap.BasemapRepository;
import java.util.List;
import java.util.Objects;

public final class ListBasemapUseCase {
    private final BasemapRepository repository;

    public ListBasemapUseCase(BasemapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Basemap> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
