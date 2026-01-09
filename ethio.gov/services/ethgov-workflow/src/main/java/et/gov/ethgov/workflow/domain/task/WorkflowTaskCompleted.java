package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;

public record WorkflowTaskCompleted(
        WorkflowTaskId taskId, String outcome, Instant occurredAt)
        implements WorkflowTaskDomainEvent {}
