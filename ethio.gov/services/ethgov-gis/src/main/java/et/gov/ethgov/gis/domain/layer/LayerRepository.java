package et.gov.ethgov.gis.domain.layer;

import java.util.List;
import java.util.Optional;

public interface LayerRepository {
    Layer save(Layer aggregate);

    Optional<Layer> findById(LayerId id);

    Optional<Layer> findByReferenceCode(String tenantId, String referenceCode);

    List<Layer> findByTenant(String tenantId, int limit);

    void delete(LayerId id);
}
