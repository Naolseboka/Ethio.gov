package et.gov.ethgov.gis.domain.zone;

import java.util.List;
import java.util.Optional;

public interface ZoneRepository {
    Zone save(Zone aggregate);

    Optional<Zone> findById(ZoneId id);

    Optional<Zone> findByReferenceCode(String tenantId, String referenceCode);

    List<Zone> findByTenant(String tenantId, int limit);

    void delete(ZoneId id);
}
