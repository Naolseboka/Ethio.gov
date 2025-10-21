package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.instance.ProcessInstance;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceRepository;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelProcessInstanceUseCase {

    private final ProcessInstanceRepository instances;
    private final WorkflowTaskRepository tasks;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public CancelProcessInstanceUseCase(
            ProcessInstanceRepository instances,
            WorkflowTaskRepository tasks,
            WorkflowEventPublisher eventPublisher,
            Clock clock) {
        this.instances = instances;
        this.tasks = tasks;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String processInstanceId, String reason) {
        Objects.requireNonNull(processInstanceId, "processInstanceId");
        ProcessInstance instance = instances
                .findById(ProcessInstanceId.of(processInstanceId))
                .orElseThrow(() -> new DomainException(
                        "PROCESS_NOT_FOUND",
                        "Process Not Found",
                        "Process instance does not exist",
                        404));
        instance.cancel(reason, clock.instant());
        instances.save(instance);
        tasks.findOpenOrClaimedByProcess(instance.id()).ifPresent(task -> {
            task.cancel(clock.instant());
            tasks.save(task);
            eventPublisher.publishTaskEvents(task.pullDomainEvents());
        });
        eventPublisher.publishInstanceEvents(instance.pullDomainEvents());
    }
}
