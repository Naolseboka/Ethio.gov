package et.gov.ethgov.opendata.domain.catalog;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository {
    Catalog save(Catalog aggregate);

    Optional<Catalog> findById(CatalogId id);

    Optional<Catalog> findByReferenceCode(String tenantId, String referenceCode);

    List<Catalog> findByTenant(String tenantId, int limit);

    void delete(CatalogId id);
}
