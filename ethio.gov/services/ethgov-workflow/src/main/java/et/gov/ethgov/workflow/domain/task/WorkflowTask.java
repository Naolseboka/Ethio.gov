package et.gov.ethgov.workflow.domain.task;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.domain.instance.ProcessInstanceId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class WorkflowTask {

    private final WorkflowTaskId id;
    private final TenantId tenantId;
    private final ProcessInstanceId processInstanceId;
    private final String stepKey;
    private final String name;
    private final String assigneeRole;
    private WorkflowTaskStatus status;
    private String claimedByAccountId;
    private String outcome;
    private final Instant createdAt;
    private Instant completedAt;
    private Instant updatedAt;
    private long entityVersion;
    private final List<WorkflowTaskDomainEvent> domainEvents = new ArrayList<>();

    private WorkflowTask(
            WorkflowTaskId id,
            TenantId tenantId,
            ProcessInstanceId processInstanceId,
            String stepKey,
            String name,
            String assigneeRole,
            WorkflowTaskStatus status,
            String claimedByAccountId,
            String outcome,
            Instant createdAt,
            Instant completedAt,
            Instant updatedAt,
            long entityVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.processInstanceId = processInstanceId;
        this.stepKey = stepKey;
        this.name = name;
        this.assigneeRole = assigneeRole;
        this.status = status;
        this.claimedByAccountId = claimedByAccountId;
        this.outcome = outcome;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
        this.entityVersion = entityVersion;
    }

    public static WorkflowTask open(
            TenantId tenantId,
            ProcessInstanceId processInstanceId,
            String stepKey,
            String name,
            String assigneeRole,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(processInstanceId, "processInstanceId");
        Objects.requireNonNull(stepKey, "stepKey");
        Objects.requireNonNull(name, "name");
        WorkflowTask task = new WorkflowTask(
                WorkflowTaskId.generate(),
                tenantId,
                processInstanceId,
                stepKey,
                name,
                assigneeRole,
                WorkflowTaskStatus.OPEN,
                null,
                null,
                now,
                null,
                now,
                0);
        task.record(new WorkflowTaskCreated(task.id, processInstanceId, stepKey, now));
        return task;
    }

    public static WorkflowTask rehydrate(
            WorkflowTaskId id,
            TenantId tenantId,
            ProcessInstanceId processInstanceId,
            String stepKey,
            String name,
            String assigneeRole,
            WorkflowTaskStatus status,
            String claimedByAccountId,
            String outcome,
            Instant createdAt,
            Instant completedAt,
            Instant updatedAt,
            long entityVersion) {
        return new WorkflowTask(
                id,
                tenantId,
                processInstanceId,
                stepKey,
                name,
                assigneeRole,
                status,
                claimedByAccountId,
                outcome,
                createdAt,
                completedAt,
                updatedAt,
                entityVersion);
    }

    public void claim(String accountId, Instant now) {
        Objects.requireNonNull(accountId, "accountId");
        if (status == WorkflowTaskStatus.COMPLETED || status == WorkflowTaskStatus.CANCELLED) {
            throw new DomainException(
                    "TASK_TERMINAL",
                    "Task Terminal",
                    "Completed or cancelled tasks cannot be claimed",
                    409);
        }
        String userId = requireUserId(accountId);
        if (status == WorkflowTaskStatus.CLAIMED) {
            if (userId.equals(claimedByAccountId)) {
                return;
            }
            throw new DomainException(
                    "TASK_ALREADY_CLAIMED",
                    "Task Already Claimed",
                    "Task is already claimed by another user",
                    409);
        }
        this.status = WorkflowTaskStatus.CLAIMED;
        this.claimedByAccountId = userId;
        touch(now);
        record(new WorkflowTaskClaimed(id, userId, now));
    }

    public void complete(String outcome, String actorAccountId, Instant now) {
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(actorAccountId, "actorAccountId");
        if (status == WorkflowTaskStatus.COMPLETED) {
            return;
        }
        if (status == WorkflowTaskStatus.CANCELLED) {
            throw new DomainException(
                    "TASK_CANCELLED",
                    "Task Cancelled",
                    "Cancelled tasks cannot be completed",
                    409);
        }
        if (status != WorkflowTaskStatus.CLAIMED) {
            throw new DomainException(
                    "TASK_NOT_CLAIMED",
                    "Task Not Claimed",
                    "Task must be claimed before completion",
                    409);
        }
        String actor = requireUserId(actorAccountId);
        if (!actor.equals(claimedByAccountId)) {
            throw new DomainException(
                    "TASK_CLAIM_MISMATCH",
                    "Task Claim Mismatch",
                    "Only the claiming user can complete the task",
                    403);
        }
        String normalizedOutcome = outcome.trim().toUpperCase(Locale.ROOT);
        if (normalizedOutcome.length() < 2 || normalizedOutcome.length() > 64) {
            throw new IllegalArgumentException("outcome length must be between 2 and 64");
        }
        this.outcome = normalizedOutcome;
        this.status = WorkflowTaskStatus.COMPLETED;
        this.completedAt = now;
        touch(now);
        record(new WorkflowTaskCompleted(id, normalizedOutcome, now));
    }

    public void cancel(Instant now) {
        if (status == WorkflowTaskStatus.COMPLETED || status == WorkflowTaskStatus.CANCELLED) {
            return;
        }
        this.status = WorkflowTaskStatus.CANCELLED;
        this.completedAt = now;
        touch(now);
        record(new WorkflowTaskCancelled(id, now));
    }

    public List<WorkflowTaskDomainEvent> pullDomainEvents() {
        List<WorkflowTaskDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private static String requireUserId(String userId) {
        String normalized = userId.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("accountId must be a UserAccountId");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(WorkflowTaskDomainEvent event) {
        domainEvents.add(event);
    }

    public WorkflowTaskId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public ProcessInstanceId processInstanceId() {
        return processInstanceId;
    }

    public String stepKey() {
        return stepKey;
    }

    public String name() {
        return name;
    }

    public Optional<String> assigneeRole() {
        return Optional.ofNullable(assigneeRole);
    }

    public WorkflowTaskStatus status() {
        return status;
    }

    public Optional<String> claimedByAccountId() {
        return Optional.ofNullable(claimedByAccountId);
    }

    public Optional<String> outcome() {
        return Optional.ofNullable(outcome);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Optional<Instant> completedAt() {
        return Optional.ofNullable(completedAt);
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return entityVersion;
    }
}
