package et.gov.ethgov.gis.application.crs;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import java.util.List;
import java.util.Objects;

public final class ListCrsUseCase {
    private final CrsRepository repository;

    public ListCrsUseCase(CrsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Crs> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
