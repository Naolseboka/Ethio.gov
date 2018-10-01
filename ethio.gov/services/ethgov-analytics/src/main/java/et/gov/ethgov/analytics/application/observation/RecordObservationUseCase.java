package et.gov.ethgov.analytics.application.observation;

import et.gov.ethgov.analytics.application.AnalyticsEventPublisher;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionId;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionRepository;
import et.gov.ethgov.analytics.domain.metric.MetricDefinitionStatus;
import et.gov.ethgov.analytics.domain.observation.MetricObservation;
import et.gov.ethgov.analytics.domain.observation.MetricObservationRepository;
import et.gov.ethgov.analytics.domain.observation.ObservationId;
import et.gov.ethgov.analytics.domain.observation.ObservationNumber;
import et.gov.ethgov.analytics.domain.observation.ObservationNumberSequence;
import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordObservationUseCase {

    private final MetricDefinitionRepository definitions;
    private final MetricObservationRepository observations;
    private final ObservationNumberSequence sequences;
    private final AnalyticsEventPublisher eventPublisher;
    private final Clock clock;

    public RecordObservationUseCase(
            MetricDefinitionRepository definitions,
            MetricObservationRepository observations,
            ObservationNumberSequence sequences,
            AnalyticsEventPublisher eventPublisher,
            Clock clock) {
        this.definitions = definitions;
        this.observations = observations;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ObservationId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        MetricDefinition definition = definitions
                .findById(MetricDefinitionId.of(command.metricDefinitionId()))
                .orElseThrow(() -> new DomainException(
                        "METRIC_NOT_FOUND", "Metric Not Found", "Metric definition does not exist", 404));
        if (!definition.tenantId().equals(tenantId)) {
            throw new DomainException(
                    "METRIC_TENANT_MISMATCH",
                    "Metric Tenant Mismatch",
                    "Metric definition does not belong to the requested tenant",
                    409);
        }
        if (definition.status() != MetricDefinitionStatus.PUBLISHED) {
            throw new DomainException(
                    "METRIC_NOT_PUBLISHED",
                    "Metric Not Published",
                    "Observations can only be recorded against PUBLISHED metrics",
                    409);
        }
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        ObservationNumber observationNumber =
                ObservationNumber.allocate(year, sequences.nextSequence(tenantId, year));
        MetricObservation observation = MetricObservation.record(
                tenantId,
                observationNumber,
                definition.id(),
                new BigDecimal(command.value()),
                Instant.parse(command.periodStart()),
                Instant.parse(command.periodEnd()),
                command.dimensionsJson(),
                command.sourceReference(),
                clock.instant());
        observations.save(observation);
        eventPublisher.publishObservationEvents(observation.pullDomainEvents());
        return observation.id();
    }

    public record Command(
            String tenantId,
            String metricDefinitionId,
            String value,
            String periodStart,
            String periodEnd,
            String dimensionsJson,
            String sourceReference) {}
}
