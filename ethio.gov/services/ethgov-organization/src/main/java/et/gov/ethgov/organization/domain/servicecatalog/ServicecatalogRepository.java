package et.gov.ethgov.organization.domain.servicecatalog;

import java.util.List;
import java.util.Optional;

public interface ServicecatalogRepository {
    Servicecatalog save(Servicecatalog aggregate);

    Optional<Servicecatalog> findById(ServicecatalogId id);

    Optional<Servicecatalog> findByReferenceCode(String tenantId, String referenceCode);

    List<Servicecatalog> findByTenant(String tenantId, int limit);

    void delete(ServicecatalogId id);
}
