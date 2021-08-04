package et.gov.ethgov.gis.domain.spatialquery;

import java.util.List;
import java.util.Optional;

public interface SpatialqueryRepository {
    Spatialquery save(Spatialquery aggregate);

    Optional<Spatialquery> findById(SpatialqueryId id);

    Optional<Spatialquery> findByReferenceCode(String tenantId, String referenceCode);

    List<Spatialquery> findByTenant(String tenantId, int limit);

    void delete(SpatialqueryId id);
}
