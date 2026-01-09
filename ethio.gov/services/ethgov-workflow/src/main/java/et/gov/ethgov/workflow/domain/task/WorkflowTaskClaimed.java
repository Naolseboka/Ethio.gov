package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;

public record WorkflowTaskClaimed(
        WorkflowTaskId taskId, String claimedByAccountId, Instant occurredAt)
        implements WorkflowTaskDomainEvent {}
