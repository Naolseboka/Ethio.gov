package et.gov.ethgov.analytics.domain.anomaly;

import java.util.List;
import java.util.Optional;

public interface AnomalyRepository {
    Anomaly save(Anomaly aggregate);

    Optional<Anomaly> findById(AnomalyId id);

    Optional<Anomaly> findByReferenceCode(String tenantId, String referenceCode);

    List<Anomaly> findByTenant(String tenantId, int limit);

    void delete(AnomalyId id);
}
