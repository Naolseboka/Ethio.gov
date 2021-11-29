package et.gov.ethgov.identity.domain.lockout;

import java.util.List;
import java.util.Optional;

public interface LockoutRepository {
    Lockout save(Lockout aggregate);

    Optional<Lockout> findById(LockoutId id);

    Optional<Lockout> findByReferenceCode(String tenantId, String referenceCode);

    List<Lockout> findByTenant(String tenantId, int limit);

    void delete(LockoutId id);
}
