package et.gov.ethgov.gis.domain.basemap;

import java.util.List;
import java.util.Optional;

public interface BasemapRepository {
    Basemap save(Basemap aggregate);

    Optional<Basemap> findById(BasemapId id);

    Optional<Basemap> findByReferenceCode(String tenantId, String referenceCode);

    List<Basemap> findByTenant(String tenantId, int limit);

    void delete(BasemapId id);
}
