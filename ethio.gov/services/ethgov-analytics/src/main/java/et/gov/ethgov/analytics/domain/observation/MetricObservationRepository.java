package et.gov.ethgov.analytics.domain.observation;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Optional;

public interface MetricObservationRepository {
    Optional<MetricObservation> findById(ObservationId id);

    Optional<MetricObservation> findByTenantAndObservationNumber(
            TenantId tenantId, ObservationNumber observationNumber);

    List<MetricObservation> findByMetricDefinitionId(MetricDefinitionId metricDefinitionId);

    void save(MetricObservation observation);
}
