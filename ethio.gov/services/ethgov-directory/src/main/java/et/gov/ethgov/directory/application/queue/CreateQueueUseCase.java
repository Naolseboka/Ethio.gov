package et.gov.ethgov.directory.application.queue;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueId;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateQueueUseCase {
    private final QueueRepository repository;
    private final Clock clock;

    public CreateQueueUseCase(QueueRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QueueId execute(CreateQueueCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Queue aggregate = Queue.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateQueueCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String queueId,
            String reason) {}
}
