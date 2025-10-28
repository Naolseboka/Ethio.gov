package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskId;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimWorkflowTaskUseCase {

    private final WorkflowTaskRepository tasks;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public ClaimWorkflowTaskUseCase(
            WorkflowTaskRepository tasks, WorkflowEventPublisher eventPublisher, Clock clock) {
        this.tasks = tasks;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String taskId, String accountId) {
        Objects.requireNonNull(taskId, "taskId");
        WorkflowTask task = tasks.findById(WorkflowTaskId.of(taskId))
                .orElseThrow(() -> new DomainException(
                        "TASK_NOT_FOUND",
                        "Task Not Found",
                        "Workflow task does not exist",
                        404));
        task.claim(accountId, clock.instant());
        tasks.save(task);
        eventPublisher.publishTaskEvents(task.pullDomainEvents());
    }
}
