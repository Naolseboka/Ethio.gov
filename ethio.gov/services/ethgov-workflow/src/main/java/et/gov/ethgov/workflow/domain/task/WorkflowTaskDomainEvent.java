package et.gov.ethgov.workflow.domain.task;

import java.time.Instant;

public sealed interface WorkflowTaskDomainEvent
        permits WorkflowTaskCreated, WorkflowTaskClaimed, WorkflowTaskCompleted, WorkflowTaskCancelled {

    WorkflowTaskId taskId();

    Instant occurredAt();
}
