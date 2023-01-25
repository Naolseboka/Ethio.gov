package et.gov.ethgov.notification.domain.suppression;

import java.util.List;
import java.util.Optional;

public interface SuppressionRepository {
    Suppression save(Suppression aggregate);

    Optional<Suppression> findById(SuppressionId id);

    Optional<Suppression> findByReferenceCode(String tenantId, String referenceCode);

    List<Suppression> findByTenant(String tenantId, int limit);

    void delete(SuppressionId id);
}
