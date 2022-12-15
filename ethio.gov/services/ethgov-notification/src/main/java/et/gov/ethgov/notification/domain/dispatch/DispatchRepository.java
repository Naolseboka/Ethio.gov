package et.gov.ethgov.notification.domain.dispatch;

import java.util.List;
import java.util.Optional;

public interface DispatchRepository {
    Dispatch save(Dispatch aggregate);

    Optional<Dispatch> findById(DispatchId id);

    Optional<Dispatch> findByReferenceCode(String tenantId, String referenceCode);

    List<Dispatch> findByTenant(String tenantId, int limit);

    void delete(DispatchId id);
}
