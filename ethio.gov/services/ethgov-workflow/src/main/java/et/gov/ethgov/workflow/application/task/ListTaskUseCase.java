package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import java.util.List;
import java.util.Objects;

public final class ListTaskUseCase {
    private final TaskRepository repository;

    public ListTaskUseCase(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Task> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
