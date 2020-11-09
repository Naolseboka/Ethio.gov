package et.gov.ethgov.document.domain.versioning;

import java.util.List;
import java.util.Optional;

public interface VersioningRepository {
    Versioning save(Versioning aggregate);

    Optional<Versioning> findById(VersioningId id);

    Optional<Versioning> findByReferenceCode(String tenantId, String referenceCode);

    List<Versioning> findByTenant(String tenantId, int limit);

    void delete(VersioningId id);
}
