package et.gov.ethgov.directory.application.queue;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueId;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateQueueUseCase {
    private final QueueRepository repository;
    private final Clock clock;

    public UpdateQueueUseCase(QueueRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QueueId execute(UpdateQueueCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QueueId id = QueueId.parse(command.queueId());
        Queue aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("queue not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateQueueCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String queueId,
            String reason) {}
}
