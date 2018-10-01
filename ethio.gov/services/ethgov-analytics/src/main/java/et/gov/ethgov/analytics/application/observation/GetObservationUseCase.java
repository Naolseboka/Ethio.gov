package et.gov.ethgov.analytics.application.observation;

import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.observation.MetricObservation;
import et.gov.ethgov.analytics.domain.observation.MetricObservationRepository;
import et.gov.ethgov.analytics.domain.observation.ObservationId;
import et.gov.ethgov.analytics.domain.observation.ObservationNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetObservationUseCase {

    private final MetricObservationRepository observations;

    public GetObservationUseCase(MetricObservationRepository observations) {
        this.observations = observations;
    }

    @Transactional(readOnly = true)
    public MetricObservation byId(String observationId) {
        return observations
                .findById(ObservationId.of(observationId))
                .orElseThrow(() -> new DomainException(
                        "OBSERVATION_NOT_FOUND",
                        "Observation Not Found",
                        "Metric observation does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public MetricObservation byNumber(String tenantId, String observationNumber) {
        return observations
                .findByTenantAndObservationNumber(
                        TenantId.of(tenantId), ObservationNumber.of(observationNumber))
                .orElseThrow(() -> new DomainException(
                        "OBSERVATION_NOT_FOUND",
                        "Observation Not Found",
                        "Metric observation does not exist",
                        404));
    }

    @Transactional(readOnly = true)
    public List<MetricObservation> byMetric(String metricDefinitionId) {
        return observations.findByMetricDefinitionId(MetricDefinitionId.of(metricDefinitionId));
    }
}
