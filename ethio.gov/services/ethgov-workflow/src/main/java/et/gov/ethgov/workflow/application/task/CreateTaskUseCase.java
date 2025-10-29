package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.workflow.domain.task.Task;
import et.gov.ethgov.workflow.domain.task.TaskId;
import et.gov.ethgov.workflow.domain.task.TaskRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTaskUseCase {
    private final TaskRepository repository;
    private final Clock clock;

    public CreateTaskUseCase(TaskRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TaskId execute(CreateTaskCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Task aggregate = Task.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTaskCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String taskId,
            String reason) {}
}
