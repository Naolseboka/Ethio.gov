package et.gov.ethgov.gis.application.gisexport;

import et.gov.ethgov.gis.domain.gisexport.Gisexport;
import et.gov.ethgov.gis.domain.gisexport.GisexportRepository;
import java.util.List;
import java.util.Objects;

public final class ListGisexportUseCase {
    private final GisexportRepository repository;

    public ListGisexportUseCase(GisexportRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Gisexport> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
