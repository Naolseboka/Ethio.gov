package et.gov.ethgov.licensing.domain.revocation;

import java.util.List;
import java.util.Optional;

public interface RevocationRepository {
    Revocation save(Revocation aggregate);

    Optional<Revocation> findById(RevocationId id);

    Optional<Revocation> findByReferenceCode(String tenantId, String referenceCode);

    List<Revocation> findByTenant(String tenantId, int limit);

    void delete(RevocationId id);
}
