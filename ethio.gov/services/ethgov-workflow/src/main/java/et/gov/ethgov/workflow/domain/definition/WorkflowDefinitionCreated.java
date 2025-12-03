package et.gov.ethgov.workflow.domain.definition;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record WorkflowDefinitionCreated(
        WorkflowDefinitionId definitionId, TenantId tenantId, DefinitionKey key, Instant occurredAt)
        implements WorkflowDefinitionDomainEvent {}
