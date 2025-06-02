package et.gov.ethgov.search.application.facet;

import et.gov.ethgov.search.domain.facet.Facet;
import et.gov.ethgov.search.domain.facet.FacetId;
import et.gov.ethgov.search.domain.facet.FacetRepository;
import java.util.Objects;

public final class GetFacetUseCase {
    private final FacetRepository repository;

    public GetFacetUseCase(FacetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Facet execute(String rawId) {
        FacetId id = FacetId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("facet not found: " + rawId));
    }
}
