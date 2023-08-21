package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.util.List;
import java.util.Objects;

public final class ListServicecatalogUseCase {
    private final ServicecatalogRepository repository;

    public ListServicecatalogUseCase(ServicecatalogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Servicecatalog> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
