package et.gov.ethgov.identity.domain.mfa;

import java.util.List;
import java.util.Optional;

public interface MfaRepository {
    Mfa save(Mfa aggregate);

    Optional<Mfa> findById(MfaId id);

    Optional<Mfa> findByReferenceCode(String tenantId, String referenceCode);

    List<Mfa> findByTenant(String tenantId, int limit);

    void delete(MfaId id);
}
