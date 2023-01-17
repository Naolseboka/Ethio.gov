package et.gov.ethgov.notification.domain.smsbatch;

import java.util.List;
import java.util.Optional;

public interface SmsbatchRepository {
    Smsbatch save(Smsbatch aggregate);

    Optional<Smsbatch> findById(SmsbatchId id);

    Optional<Smsbatch> findByReferenceCode(String tenantId, String referenceCode);

    List<Smsbatch> findByTenant(String tenantId, int limit);

    void delete(SmsbatchId id);
}
