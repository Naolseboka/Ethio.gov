package et.gov.ethgov.directory.application.queue;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueId;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendQueueUseCase {
    private final QueueRepository repository;
    private final Clock clock;

    public SuspendQueueUseCase(QueueRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QueueId execute(SuspendQueueCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QueueId id = QueueId.parse(command.queueId());
        Queue aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("queue not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendQueueCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String queueId,
            String reason) {}
}
