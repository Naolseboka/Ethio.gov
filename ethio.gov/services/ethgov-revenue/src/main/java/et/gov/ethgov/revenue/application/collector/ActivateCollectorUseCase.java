package et.gov.ethgov.revenue.application.collector;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorId;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCollectorUseCase {
    private final CollectorRepository repository;
    private final Clock clock;

    public ActivateCollectorUseCase(CollectorRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CollectorId execute(ActivateCollectorCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CollectorId id = CollectorId.parse(command.collectorId());
        Collector aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("collector not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCollectorCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String collectorId,
            String reason) {}
}
