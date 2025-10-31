package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskId;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateTaskUseCase {
    private final TaskRepository repository;
    private final Clock clock;

    public UpdateTaskUseCase(TaskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaskId execute(UpdateTaskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TaskId id = TaskId.parse(command.taskId());
        Task aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("task not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateTaskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taskId,
            String reason) {}
}
