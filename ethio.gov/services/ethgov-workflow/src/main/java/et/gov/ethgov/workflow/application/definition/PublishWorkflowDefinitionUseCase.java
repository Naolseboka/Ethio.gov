package et.gov.ethgov.workflow.application.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.workflow.application.WorkflowEventPublisher;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinition;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionRepository;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublishWorkflowDefinitionUseCase {

    private final WorkflowDefinitionRepository definitions;
    private final WorkflowEventPublisher eventPublisher;
    private final Clock clock;

    public PublishWorkflowDefinitionUseCase(
            WorkflowDefinitionRepository definitions, WorkflowEventPublisher eventPublisher, Clock clock) {
        this.definitions = definitions;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String definitionId) {
        Objects.requireNonNull(definitionId, "definitionId");
        WorkflowDefinition definition = definitions
                .findById(WorkflowDefinitionId.of(definitionId))
                .orElseThrow(() -> new DomainException(
                        "DEFINITION_NOT_FOUND",
                        "Definition Not Found",
                        "Workflow definition does not exist",
                        404));
        definition.publish(clock.instant());
        definitions.save(definition);
        eventPublisher.publishDefinitionEvents(definition.pullDomainEvents());
    }
}
