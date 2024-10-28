package et.gov.ethgov.reporting.domain.metric;

import java.util.List;
import java.util.Optional;

public interface MetricRepository {
    Metric save(Metric aggregate);

    Optional<Metric> findById(MetricId id);

    Optional<Metric> findByReferenceCode(String tenantId, String referenceCode);

    List<Metric> findByTenant(String tenantId, int limit);

    void delete(MetricId id);
}
