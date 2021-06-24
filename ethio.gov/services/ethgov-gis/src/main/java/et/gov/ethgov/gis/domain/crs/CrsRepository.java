package et.gov.ethgov.gis.domain.crs;

import java.util.List;
import java.util.Optional;

public interface CrsRepository {
    Crs save(Crs aggregate);

    Optional<Crs> findById(CrsId id);

    Optional<Crs> findByReferenceCode(String tenantId, String referenceCode);

    List<Crs> findByTenant(String tenantId, int limit);

    void delete(CrsId id);
}
