package et.gov.ethgov.identity.domain.recovery;

import java.util.List;
import java.util.Optional;

public interface RecoveryRepository {
    Recovery save(Recovery aggregate);

    Optional<Recovery> findById(RecoveryId id);

    Optional<Recovery> findByReferenceCode(String tenantId, String referenceCode);

    List<Recovery> findByTenant(String tenantId, int limit);

    void delete(RecoveryId id);
}
