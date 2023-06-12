package et.gov.ethgov.opendata.domain.resource;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    Resource save(Resource aggregate);

    Optional<Resource> findById(ResourceId id);

    Optional<Resource> findByReferenceCode(String tenantId, String referenceCode);

    List<Resource> findByTenant(String tenantId, int limit);

    void delete(ResourceId id);
}
