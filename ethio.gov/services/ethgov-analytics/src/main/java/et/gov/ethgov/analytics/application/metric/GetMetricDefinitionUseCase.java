package et.gov.ethgov.analytics.application.metric;

import et.gov.ethgov.analytics.domain.metric.MetricCode;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRepository;
import et.gov.ethgov.analytics.domain.metric.MetricNumber;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMetricDefinitionUseCase {

    private final MetricDefinitionRepository definitions;

    public GetMetricDefinitionUseCase(MetricDefinitionRepository definitions) {
        this.definitions = definitions;
    }

    @Transactional(readOnly = true)
    public MetricDefinition byId(String metricDefinitionId) {
        return definitions
                .findById(MetricDefinitionId.of(metricDefinitionId))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
    }

    @Transactional(readOnly = true)
    public MetricDefinition byNumber(String tenantId, String metricNumber) {
        return definitions
                .findByTenantAndMetricNumber(TenantId.of(tenantId), MetricNumber.of(metricNumber))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
    }

    @Transactional(readOnly = true)
    public MetricDefinition byCode(String tenantId, String code) {
        return definitions
                .findByTenantAndCode(TenantId.of(tenantId), MetricCode.of(code))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
    }
}
