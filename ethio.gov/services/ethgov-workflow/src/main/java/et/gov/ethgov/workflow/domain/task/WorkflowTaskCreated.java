package et.gov.ethgov.workflow.domain.task;

import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import java.time.Instant;

public record WorkflowTaskCreated(
        WorkflowTaskId taskId,
        ProcessInstanceId processInstanceId,
        String stepKey,
        Instant occurredAt)
        implements WorkflowTaskDomainEvent {}
