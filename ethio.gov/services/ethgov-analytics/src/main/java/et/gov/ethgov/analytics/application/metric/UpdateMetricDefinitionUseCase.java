package et.gov.ethgov.analytics.application.metric;

import et.gov.ethgov.analytics.application.AnalyticsEventPublisher;
import et.gov.ethgov.analytics.domain.metric.AggregationType;
import et.gov.ethgov.analytics.domain.metric.MetricCategory;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRepository;
import et.gov.ethgov.analytics.domain.metric.MetricUnit;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateMetricDefinitionUseCase {

    private final MetricDefinitionRepository definitions;
    private final AnalyticsEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateMetricDefinitionUseCase(
            MetricDefinitionRepository definitions, AnalyticsEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        MetricDefinition definition = definitions
                .findById(MetricDefinitionId.of(command.metricDefinitionId()))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
        definition.update(
                command.name(),
                command.description(),
                MetricCategory.valueOf(command.category()),
                MetricUnit.valueOf(command.unit()),
                AggregationType.valueOf(command.aggregation()),
                clock.instant());
        definitions.save(definition);
        eventPublisher.publishMetricEvents(definition.pullDomainEvents());
    }

    public record Command(
            String metricDefinitionId,
            String name,
            String description,
            String category,
            String unit,
            String aggregation) {}
}
