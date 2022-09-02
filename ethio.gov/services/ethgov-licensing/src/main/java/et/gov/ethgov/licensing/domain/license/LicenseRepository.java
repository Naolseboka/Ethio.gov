package et.gov.ethgov.licensing.domain.license;

import java.util.List;
import java.util.Optional;

public interface LicenseRepository {
    License save(License aggregate);

    Optional<License> findById(LicenseId id);

    Optional<License> findByReferenceCode(String tenantId, String referenceCode);

    List<License> findByTenant(String tenantId, int limit);

    void delete(LicenseId id);
}
