package et.gov.ethgov.opendata.application.catalog;

import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogRepository;
import java.util.List;
import java.util.Objects;

public final class ListCatalogUseCase {
    private final CatalogRepository repository;

    public ListCatalogUseCase(CatalogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Catalog> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
