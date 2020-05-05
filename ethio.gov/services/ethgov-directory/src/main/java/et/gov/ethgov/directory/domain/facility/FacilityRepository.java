package et.gov.ethgov.directory.domain.facility;

import java.util.List;
import java.util.Optional;

public interface FacilityRepository {
    Facility save(Facility aggregate);

    Optional<Facility> findById(FacilityId id);

    Optional<Facility> findByReferenceCode(String tenantId, String referenceCode);

    List<Facility> findByTenant(String tenantId, int limit);

    void delete(FacilityId id);
}
