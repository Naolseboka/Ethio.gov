package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;

public record ProcessInstanceCancelled(
        ProcessInstanceId processInstanceId, String reason, Instant occurredAt)
        implements ProcessInstanceDomainEvent {}
