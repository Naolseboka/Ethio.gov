package et.gov.ethgov.workflow.application.instance;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.definition.DefinitionKey;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionRepository;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionStatus;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
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
public class StartProcessInstanceUseCase {

    private final WorkflowDefinitionRepository definitions;
    private final ProcessInstanceRepository instances;
    private final WorkflowTaskRepository tasks;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public StartProcessInstanceUseCase(
            WorkflowDefinitionRepository definitions,
            ProcessInstanceRepository instances,
            WorkflowTaskRepository tasks,
            WorkflowEventPublisher eventPublisher,
            Clock clock) {
        this.definitions = definitions;
        this.instances = instances;
        this.tasks = tasks;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ProcessInstanceId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        WorkflowDefinition definition = definitions
                .findPublishedByTenantAndKey(tenantId, DefinitionKey.of(command.definitionKey()))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Published workflow definition does not exist for key",
                        404));
        if (definition.status() != WorkflowDefinitionStatus.PUBLISHED) {
            throw new DomainException(
                    "DEFINITION_NOT_PUBLISHED",
                    "Definition Not Published",
                    "Only published definitions can start process instances",
                    409);
        }
        if (instances.existsByTenantAndBusinessKey(tenantId, command.businessKey().trim())) {
            throw new DomainException(
                    "BUSINESS_KEY_EXISTS",
                    "Business Key Exists",
                    "A process instance with this business key already exists for the tenant",
                    409);
        }
        WorkflowStep first = definition.requireStep(0);
        ProcessInstance instance = ProcessInstance.start(
                tenantId,
                definition.id(),
                command.businessKey(),
                command.startedByAccountId(),
                first,
                clock.instant());
        instances.save(instance);
        WorkflowTask task = WorkflowTask.open(
                tenantId,
                instance.id(),
                first.stepKey(),
                first.name(),
                first.assigneeRole().orElse(null),
                clock.instant());
        tasks.save(task);
        eventPublisher.publishInstanceEvents(instance.pullDomainEvents());
        eventPublisher.publishTaskEvents(task.pullDomainEvents());
        return instance.id();
    }

    public record Command(
            String tenantId, String definitionKey, String businessKey, String startedByAccountId) {}
}
