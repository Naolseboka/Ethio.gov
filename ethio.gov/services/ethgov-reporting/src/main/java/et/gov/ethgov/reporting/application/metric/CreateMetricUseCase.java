package et.gov.ethgov.reporting.application.metric;

import et.gov.ethgov.reporting.domain.metric.Metric;
import et.gov.ethgov.reporting.domain.metric.MetricId;
import et.gov.ethgov.reporting.domain.metric.MetricRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMetricUseCase {
    private final MetricRepository repository;
    private final Clock clock;

    public CreateMetricUseCase(MetricRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MetricId execute(CreateMetricCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Metric aggregate = Metric.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMetricCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String metricId,
            String reason) {}
}
