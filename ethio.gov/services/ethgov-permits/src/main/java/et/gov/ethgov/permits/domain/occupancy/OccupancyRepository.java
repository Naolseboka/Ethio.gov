package et.gov.ethgov.permits.domain.occupancy;

import java.util.List;
import java.util.Optional;

public interface OccupancyRepository {
    Occupancy save(Occupancy aggregate);

    Optional<Occupancy> findById(OccupancyId id);

    Optional<Occupancy> findByReferenceCode(String tenantId, String referenceCode);

    List<Occupancy> findByTenant(String tenantId, int limit);

    void delete(OccupancyId id);
}
