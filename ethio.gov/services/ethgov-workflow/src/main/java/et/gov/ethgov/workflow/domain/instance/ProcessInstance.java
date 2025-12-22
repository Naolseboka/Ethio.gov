package et.gov.ethgov.workflow.domain.instance;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.workflow.domain.definition.WorkflowDefinitionId;
import et.gov.ethgov.workflow.domain.definition.WorkflowStep;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class ProcessInstance {

    private final ProcessInstanceId id;
    private final TenantId tenantId;
    private final WorkflowDefinitionId definitionId;
    private final String businessKey;
    private final String startedByAccountId;
    private ProcessInstanceStatus status;
    private int currentStepOrdinal;
    private String currentStepKey;
    private String cancellationReason;
    private final Instant startedAt;
    private Instant completedAt;
    private Instant updatedAt;
    private long entityVersion;
    private final List<ProcessInstanceDomainEvent> domainEvents = new ArrayList<>();

    private ProcessInstance(
            ProcessInstanceId id,
            TenantId tenantId,
            WorkflowDefinitionId definitionId,
            String businessKey,
            String startedByAccountId,
            ProcessInstanceStatus status,
            int currentStepOrdinal,
            String currentStepKey,
            String cancellationReason,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt,
            long entityVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.definitionId = definitionId;
        this.businessKey = businessKey;
        this.startedByAccountId = startedByAccountId;
        this.status = status;
        this.currentStepOrdinal = currentStepOrdinal;
        this.currentStepKey = currentStepKey;
        this.cancellationReason = cancellationReason;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.updatedAt = updatedAt;
        this.entityVersion = entityVersion;
    }

    public static ProcessInstance start(
            TenantId tenantId,
            WorkflowDefinitionId definitionId,
            String businessKey,
            String startedByAccountId,
            WorkflowStep firstStep,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(definitionId, "definitionId");
        Objects.requireNonNull(firstStep, "firstStep");
        ProcessInstance instance = new ProcessInstance(
                ProcessInstanceId.generate(),
                tenantId,
                definitionId,
                requireBusinessKey(businessKey),
                requireUserId(startedByAccountId),
                ProcessInstanceStatus.RUNNING,
                firstStep.ordinal(),
                firstStep.stepKey(),
                null,
                now,
                null,
                now,
                0);
        instance.record(new ProcessInstanceStarted(
                instance.id, tenantId, definitionId, instance.businessKey, now));
        instance.record(new ProcessStepAdvanced(
                instance.id, firstStep.ordinal(), firstStep.stepKey(), now));
        return instance;
    }

    public static ProcessInstance rehydrate(
            ProcessInstanceId id,
            TenantId tenantId,
            WorkflowDefinitionId definitionId,
            String businessKey,
            String startedByAccountId,
            ProcessInstanceStatus status,
            int currentStepOrdinal,
            String currentStepKey,
            String cancellationReason,
            Instant startedAt,
            Instant completedAt,
            Instant updatedAt,
            long entityVersion) {
        return new ProcessInstance(
                id,
                tenantId,
                definitionId,
                businessKey,
                startedByAccountId,
                status,
                currentStepOrdinal,
                currentStepKey,
                cancellationReason,
                startedAt,
                completedAt,
                updatedAt,
                entityVersion);
    }

    public void advanceTo(WorkflowStep nextStep, Instant now) {
        ensureRunning();
        Objects.requireNonNull(nextStep, "nextStep");
        this.currentStepOrdinal = nextStep.ordinal();
        this.currentStepKey = nextStep.stepKey();
        touch(now);
        record(new ProcessStepAdvanced(id, nextStep.ordinal(), nextStep.stepKey(), now));
    }

    public void complete(Instant now) {
        ensureRunning();
        this.status = ProcessInstanceStatus.COMPLETED;
        this.completedAt = now;
        touch(now);
        record(new ProcessInstanceCompleted(id, now));
    }

    public void cancel(String reason, Instant now) {
        ensureRunning();
        Objects.requireNonNull(reason, "reason");
        String normalized = reason.trim();
        if (normalized.length() < 5 || normalized.length() > 500) {
            throw new IllegalArgumentException("cancellation reason length must be between 5 and 500");
        }
        this.status = ProcessInstanceStatus.CANCELLED;
        this.cancellationReason = normalized;
        this.completedAt = now;
        touch(now);
        record(new ProcessInstanceCancelled(id, normalized, now));
    }

    public List<ProcessInstanceDomainEvent> pullDomainEvents() {
        List<ProcessInstanceDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private void ensureRunning() {
        if (status != ProcessInstanceStatus.RUNNING) {
            throw new DomainException(
                    "PROCESS_NOT_RUNNING",
                    "Process Not Running",
                    "Only running process instances can be modified",
                    409);
        }
    }

    private static String requireBusinessKey(String businessKey) {
        Objects.requireNonNull(businessKey, "businessKey");
        String normalized = businessKey.trim();
        if (normalized.length() < 2 || normalized.length() > 128) {
            throw new IllegalArgumentException("businessKey length must be between 2 and 128");
        }
        return normalized;
    }

    private static String requireUserId(String userId) {
        Objects.requireNonNull(userId, "startedByAccountId");
        String normalized = userId.trim();
        if (!normalized.startsWith("usr_") || normalized.length() < 40) {
            throw new IllegalArgumentException("startedByAccountId must be a UserAccountId");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(ProcessInstanceDomainEvent event) {
        domainEvents.add(event);
    }

    public ProcessInstanceId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public WorkflowDefinitionId definitionId() {
        return definitionId;
    }

    public String businessKey() {
        return businessKey;
    }

    public String startedByAccountId() {
        return startedByAccountId;
    }

    public ProcessInstanceStatus status() {
        return status;
    }

    public int currentStepOrdinal() {
        return currentStepOrdinal;
    }

    public String currentStepKey() {
        return currentStepKey;
    }

    public Optional<String> cancellationReason() {
        return Optional.ofNullable(cancellationReason);
    }

    public Instant startedAt() {
        return startedAt;
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
