package et.gov.ethgov.analytics.domain.metric;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.Optional;

public interface MetricDefinitionRepository {
    boolean existsByTenantAndCode(TenantId tenantId, MetricCode code);

    Optional<MetricDefinition> findById(MetricDefinitionId id);

    Optional<MetricDefinition> findByTenantAndMetricNumber(TenantId tenantId, MetricNumber metricNumber);

    Optional<MetricDefinition> findByTenantAndCode(TenantId tenantId, MetricCode code);

    void save(MetricDefinition definition);
}
