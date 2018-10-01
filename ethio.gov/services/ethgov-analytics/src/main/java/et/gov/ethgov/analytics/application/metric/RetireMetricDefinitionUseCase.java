package et.gov.ethgov.analytics.application.metric;

import et.gov.ethgov.analytics.application.AnalyticsEventPublisher;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireMetricDefinitionUseCase {

    private final MetricDefinitionRepository definitions;
    private final AnalyticsEventPublisher eventPublisher;
    private final Clock clock;

    public RetireMetricDefinitionUseCase(
            MetricDefinitionRepository definitions, AnalyticsEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String metricDefinitionId) {
        Objects.requireNonNull(metricDefinitionId, "metricDefinitionId");
        MetricDefinition definition = definitions
                .findById(MetricDefinitionId.of(metricDefinitionId))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
        definition.retire(clock.instant());
        definitions.save(definition);
        eventPublisher.publishMetricEvents(definition.pullDomainEvents());
    }
}
