package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;

public record WorkflowTaskCancelled(WorkflowTaskId taskId, Instant occurredAt)
        implements WorkflowTaskDomainEvent {}
