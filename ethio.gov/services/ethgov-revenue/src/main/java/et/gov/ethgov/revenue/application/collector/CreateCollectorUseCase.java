package et.gov.ethgov.revenue.application.collector;

import et.gov.ethgov.revenue.domain.collector.Collector;
import et.gov.ethgov.revenue.domain.collector.CollectorId;
import et.gov.ethgov.revenue.domain.collector.CollectorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCollectorUseCase {
    private final CollectorRepository repository;
    private final Clock clock;

    public CreateCollectorUseCase(CollectorRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CollectorId execute(CreateCollectorCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Collector aggregate = Collector.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCollectorCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String collectorId,
            String reason) {}
}
