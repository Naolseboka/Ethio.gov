package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;

public record ProcessInstanceCompleted(ProcessInstanceId processInstanceId, Instant occurredAt)
        implements ProcessInstanceDomainEvent {}
