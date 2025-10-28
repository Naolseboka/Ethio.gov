package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskId;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTaskUseCase {
    private final TaskRepository repository;
    private final Clock clock;

    public ActivateTaskUseCase(TaskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaskId execute(ActivateTaskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TaskId id = TaskId.parse(command.taskId());
        Task aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("task not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTaskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taskId,
            String reason) {}
}
