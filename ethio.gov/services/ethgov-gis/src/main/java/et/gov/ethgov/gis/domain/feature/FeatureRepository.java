package et.gov.ethgov.gis.domain.feature;

import java.util.List;
import java.util.Optional;

public interface FeatureRepository {
    Feature save(Feature aggregate);

    Optional<Feature> findById(FeatureId id);

    Optional<Feature> findByReferenceCode(String tenantId, String referenceCode);

    List<Feature> findByTenant(String tenantId, int limit);

    void delete(FeatureId id);
}
