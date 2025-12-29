package et.gov.ethgov.workflow.domain.instance;

import java.time.Instant;

public sealed interface ProcessInstanceDomainEvent
        permits ProcessInstanceStarted, ProcessInstanceCompleted, ProcessInstanceCancelled, ProcessStepAdvanced {

    ProcessInstanceId processInstanceId();

    Instant occurredAt();
}
