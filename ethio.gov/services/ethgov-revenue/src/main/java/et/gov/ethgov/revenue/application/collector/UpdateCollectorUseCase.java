package et.gov.ethgov.revenue.application.collector;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorId;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCollectorUseCase {
    private final CollectorRepository repository;
    private final Clock clock;

    public UpdateCollectorUseCase(CollectorRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CollectorId execute(UpdateCollectorCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CollectorId id = CollectorId.parse(command.collectorId());
        Collector aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("collector not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCollectorCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String collectorId,
            String reason) {}
}
