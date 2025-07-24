package et.gov.ethgov.search.domain.facet;

import java.util.List;
import java.util.Optional;

public interface FacetRepository {
    Facet save(Facet aggregate);

    Optional<Facet> findById(FacetId id);

    Optional<Facet> findByReferenceCode(String tenantId, String referenceCode);

    List<Facet> findByTenant(String tenantId, int limit);

    void delete(FacetId id);
}
