package et.gov.ethgov.directory.application.queue;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueId;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import java.util.Objects;

public final class GetQueueUseCase {
    private final QueueRepository repository;

    public GetQueueUseCase(QueueRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Queue execute(String rawId) {
        QueueId id = QueueId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("queue not found: " + rawId));
    }
}
