package et.gov.ethgov.opendata.application.catalog;

import et.gov.ethgov.opendata.domain.catalog.Catalog;
import et.gov.ethgov.opendata.domain.catalog.CatalogId;
import et.gov.ethgov.opendata.domain.catalog.CatalogRepository;
import java.util.Objects;

public final class GetCatalogUseCase {
    private final CatalogRepository repository;

    public GetCatalogUseCase(CatalogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Catalog execute(String rawId) {
        CatalogId id = CatalogId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("catalog not found: " + rawId));
    }
}
