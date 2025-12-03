package et.gov.ethgov.workflow.domain.definition;

import java.time.Instant;

public sealed interface WorkflowDefinitionDomainEvent
        permits WorkflowDefinitionCreated, WorkflowDefinitionPublished, WorkflowDefinitionRetired {

    WorkflowDefinitionId definitionId();

    Instant occurredAt();
}
