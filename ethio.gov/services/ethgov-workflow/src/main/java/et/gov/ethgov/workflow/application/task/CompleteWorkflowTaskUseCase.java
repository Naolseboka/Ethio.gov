package et.gov.ethgov.workflow.application.task;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionRepository;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
import et.gov.ethgov.workflow.domain.instance.ProcessInstance;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceRepository;
import et.gov.ethgov.workflow.domain.task.WorkflowTask;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskId;
import et.gov.ethgov.workflow.domain.task.WorkflowTaskRepository;
import java.time.Clock;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteWorkflowTaskUseCase {

    private final WorkflowTaskRepository tasks;
    private final ProcessInstanceRepository instances;
    private final WorkflowDefinitionRepository definitions;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public CompleteWorkflowTaskUseCase(
            WorkflowTaskRepository tasks,
            ProcessInstanceRepository instances,
            WorkflowDefinitionRepository definitions,
            WorkflowEventPublisher eventPublisher,
            Clock clock) {
        this.tasks = tasks;
        this.instances = instances;
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String taskId, String outcome, String actorAccountId) {
        Objects.requireNonNull(taskId, "taskId");
        WorkflowTask task = tasks.findById(WorkflowTaskId.of(taskId))
                .orElseThrow(() -> new DomainException(
                        "TASK_NOT_FOUND",
                        "Task Not Found",
                        "Workflow task does not exist",
                        404));
        ProcessInstance instance = instances
                .findById(task.processInstanceId())
                .orElseThrow(() -> new DomainException(
                        "PROCESS_NOT_FOUND",
                        "Process Not Found",
                        "Process instance does not exist",
                        404));
        task.complete(outcome, actorAccountId, clock.instant());
        tasks.save(task);
        eventPublisher.publishTaskEvents(task.pullDomainEvents());

        WorkflowDefinition definition = definitions
                .findById(instance.definitionId())
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Workflow definition does not exist",
                        404));

        if (definition.isLastStep(instance.currentStepOrdinal())) {
            instance.complete(clock.instant());
            instances.save(instance);
            eventPublisher.publishInstanceEvents(instance.pullDomainEvents());
            return;
        }

        Optional<WorkflowStep> next = definition.nextStep(instance.currentStepOrdinal());
        WorkflowStep nextStep = next.orElseThrow(() -> new DomainException(
                "STEP_NOT_FOUND",
                "Step Not Found",
                "Next workflow step does not exist",
                500));
        instance.advanceTo(nextStep, clock.instant());
        instances.save(instance);
        WorkflowTask nextTask = WorkflowTask.open(
                instance.tenantId(),
                instance.id(),
                nextStep.stepKey(),
                nextStep.name(),
                nextStep.assigneeRole().orElse(null),
                clock.instant());
        tasks.save(nextTask);
        eventPublisher.publishInstanceEvents(instance.pullDomainEvents());
        eventPublisher.publishTaskEvents(nextTask.pullDomainEvents());
    }
}
