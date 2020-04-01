package et.gov.ethgov.directory.application.queue;

import et.gov.ethgov.directory.domain.queue.Queue;
import et.gov.ethgov.directory.domain.queue.QueueRepository;
import java.util.List;
import java.util.Objects;

public final class ListQueueUseCase {
    private final QueueRepository repository;

    public ListQueueUseCase(QueueRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Queue> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
