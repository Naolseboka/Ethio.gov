package et.gov.ethgov.search.application.facet;

import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetRepository;
import java.util.List;
import java.util.Objects;

public final class ListFacetUseCase {
    private final FacetRepository repository;

    public ListFacetUseCase(FacetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Facet> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
