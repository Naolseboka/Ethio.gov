package et.gov.ethgov.workflow.domain.instance;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import java.time.Instant;

public record ProcessInstanceStarted(
        ProcessInstanceId processInstanceId,
        TenantId tenantId,
        WorkflowDefinitionId definitionId,
        String businessKey,
        Instant occurredAt)
        implements ProcessInstanceDomainEvent {}
