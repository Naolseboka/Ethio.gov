package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;

public record ProcessStepAdvanced(
        ProcessInstanceId processInstanceId, int stepOrdinal, String stepKey, Instant occurredAt)
        implements ProcessInstanceDomainEvent {}
