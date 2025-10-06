package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.definition.DefinitionKey;
import et.gov.ethgov.workflow.domain.definition.StepType;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionRepository;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWorkflowDefinitionUseCase {

    private final WorkflowDefinitionRepository definitions;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public CreateWorkflowDefinitionUseCase(
            WorkflowDefinitionRepository definitions, WorkflowEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public WorkflowDefinitionId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        DefinitionKey key = DefinitionKey.of(command.key());
        if (definitions.existsByTenantAndKey(tenantId, key)) {
            throw new DomainException(
                    "DEFINITION_KEY_EXISTS",
                    "Definition Key Exists",
                    "A workflow definition with this key already exists for the tenant",
                    409);
        }
        List<WorkflowStep> steps = new ArrayList<>();
        for (int i = 0; i < command.steps().size(); i++) {
            StepCommand step = command.steps().get(i);
            steps.add(WorkflowStep.of(
                    step.stepKey(),
                    step.name(),
                    StepType.valueOf(step.type()),
                    step.assigneeRole(),
                    i));
        }
        WorkflowDefinition definition = WorkflowDefinition.create(
                tenantId, key, command.name(), command.description(), steps, clock.instant());
        definitions.save(definition);
        eventPublisher.publishDefinitionEvents(definition.pullDomainEvents());
        return definition.id();
    }

    public record Command(
            String tenantId, String key, String name, String description, List<StepCommand> steps) {}

    public record StepCommand(String stepKey, String name, String type, String assigneeRole) {}
}
