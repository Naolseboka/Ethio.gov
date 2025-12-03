package et.gov.ethgov.workflow.domain.definition;

import java.time.Instant;

public record WorkflowDefinitionPublished(WorkflowDefinitionId definitionId, Instant occurredAt)
        implements WorkflowDefinitionDomainEvent {}
