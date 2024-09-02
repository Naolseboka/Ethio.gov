package et.gov.ethgov.reporting.application.metric;

import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricId;
import et.gov.ethgov.reporting.domain.metric.MetricRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateMetricUseCase {
    private final MetricRepository repository;
    private final Clock clock;

    public UpdateMetricUseCase(MetricRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MetricId execute(UpdateMetricCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MetricId id = MetricId.parse(command.metricId());
        Metric aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("metric not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateMetricCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String metricId,
            String reason) {}
}
