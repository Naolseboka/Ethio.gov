package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskId;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import java.util.Objects;

public final class GetTaskUseCase {
    private final TaskRepository repository;

    public GetTaskUseCase(TaskRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Task execute(String rawId) {
        TaskId id = TaskId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("task not found: " + rawId));
    }
}
