package et.gov.ethgov.analytics.application.metric;

import et.gov.ethgov.analytics.application.AnalyticsEventPublisher;
import et.gov.ethgov.analytics.domain.metric.AggregationType;
import et.gov.ethgov.analytics.domain.metric.MetricCategory;
import et.gov.ethgov.analytics.domain.metric.MetricCode;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRepository;
import et.gov.ethgov.analytics.domain.metric.MetricNumber;
import et.gov.ethgov.analytics.domain.metric.MetricNumberSequence;
import et.gov.ethgov.analytics.domain.metric.MetricUnit;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterMetricDefinitionUseCase {

    private final MetricDefinitionRepository definitions;
    private final MetricNumberSequence sequences;
    private final AnalyticsEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterMetricDefinitionUseCase(
            MetricDefinitionRepository definitions,
            MetricNumberSequence sequences,
            AnalyticsEventPublisher eventPublisher,
            Clock clock) {
        this.definitions = definitions;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public MetricDefinitionId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        MetricCode code = MetricCode.of(command.code());
        if (definitions.existsByTenantAndCode(tenantId, code)) {
            throw new DomainException(
                    "METRIC_CODE_EXISTS",
                    "Metric Code Exists",
                    "A metric definition with this code already exists for the tenant",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        MetricNumber metricNumber = MetricNumber.allocate(year, sequences.nextSequence(tenantId, year));
        MetricDefinition definition = MetricDefinition.register(
                tenantId,
                metricNumber,
                code,
                command.name(),
                command.description(),
                MetricCategory.valueOf(command.category()),
                MetricUnit.valueOf(command.unit()),
                AggregationType.valueOf(command.aggregation()),
                clock.instant());
        definitions.save(definition);
        eventPublisher.publishMetricEvents(definition.pullDomainEvents());
        return definition.id();
    }

    public record Command(
            String tenantId,
            String code,
            String name,
            String description,
            String category,
            String unit,
            String aggregation) {}
}
