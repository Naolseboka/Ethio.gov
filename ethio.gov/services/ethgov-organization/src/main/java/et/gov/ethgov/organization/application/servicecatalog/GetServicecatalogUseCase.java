package et.gov.ethgov.organization.application.servicecatalog;

import et.gov.ethgov.organization.domain.servicecatalog.Servicecatalog;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogId;
import et.gov.ethgov.organization.domain.servicecatalog.ServicecatalogRepository;
import java.util.Objects;

public final class GetServicecatalogUseCase {
    private final ServicecatalogRepository repository;

    public GetServicecatalogUseCase(ServicecatalogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Servicecatalog execute(String rawId) {
        ServicecatalogId id = ServicecatalogId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("servicecatalog not found: " + rawId));
    }
}
