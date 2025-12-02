package et.gov.ethgov.workflow.domain.definition;

import et.gov.ethgov.platform.commons.error.DomainException;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class WorkflowDefinition {

    private final WorkflowDefinitionId id;
    private final TenantId tenantId;
    private final DefinitionKey key;
    private String name;
    private String description;
    private final int version;
    private WorkflowDefinitionStatus status;
    private final List<WorkflowStep> steps;
    private final Instant createdAt;
    private Instant updatedAt;
    private long entityVersion;
    private final List<WorkflowDefinitionDomainEvent> domainEvents = new ArrayList<>();

    private WorkflowDefinition(
            WorkflowDefinitionId id,
            TenantId tenantId,
            DefinitionKey key,
            String name,
            String description,
            int version,
            WorkflowDefinitionStatus status,
            List<WorkflowStep> steps,
            Instant createdAt,
            Instant updatedAt,
            long entityVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.key = key;
        this.name = name;
        this.description = description;
        this.version = version;
        this.status = status;
        this.steps = List.copyOf(steps);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.entityVersion = entityVersion;
    }

    public static WorkflowDefinition create(
            TenantId tenantId,
            DefinitionKey key,
            String name,
            String description,
            List<WorkflowStep> steps,
            Instant now) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(steps, "steps");
        validateSteps(steps);
        WorkflowDefinition definition = new WorkflowDefinition(
                WorkflowDefinitionId.generate(),
                tenantId,
                key,
                requireName(name),
                normalizeOptional(description, 500),
                1,
                WorkflowDefinitionStatus.DRAFT,
                steps,
                now,
                now,
                0);
        definition.record(new WorkflowDefinitionCreated(definition.id, tenantId, key, now));
        return definition;
    }

    public static WorkflowDefinition rehydrate(
            WorkflowDefinitionId id,
            TenantId tenantId,
            DefinitionKey key,
            String name,
            String description,
            int version,
            WorkflowDefinitionStatus status,
            List<WorkflowStep> steps,
            Instant createdAt,
            Instant updatedAt,
            long entityVersion) {
        return new WorkflowDefinition(
                id, tenantId, key, name, description, version, status, steps, createdAt, updatedAt, entityVersion);
    }

    public void publish(Instant now) {
        if (status == WorkflowDefinitionStatus.RETIRED) {
            throw new DomainException(
                    "DEFINITION_RETIRED",
                    "Definition Retired",
                    "Retired workflow definitions cannot be published",
                    409);
        }
        if (status == WorkflowDefinitionStatus.PUBLISHED) {
            return;
        }
        if (steps.isEmpty()) {
            throw new DomainException(
                    "DEFINITION_HAS_NO_STEPS",
                    "Definition Has No Steps",
                    "A workflow definition must have at least one step to publish",
                    409);
        }
        this.status = WorkflowDefinitionStatus.PUBLISHED;
        touch(now);
        record(new WorkflowDefinitionPublished(id, now));
    }

    public void retire(Instant now) {
        if (status != WorkflowDefinitionStatus.PUBLISHED) {
            throw new DomainException(
                    "DEFINITION_NOT_PUBLISHED",
                    "Definition Not Published",
                    "Only published workflow definitions can be retired",
                    409);
        }
        this.status = WorkflowDefinitionStatus.RETIRED;
        touch(now);
        record(new WorkflowDefinitionRetired(id, now));
    }

    public WorkflowStep requireStep(int ordinal) {
        return steps.stream()
                .filter(step -> step.ordinal() == ordinal)
                .findFirst()
                .orElseThrow(() -> new DomainException(
                        "STEP_NOT_FOUND",
                        "Step Not Found",
                        "Workflow step does not exist at ordinal " + ordinal,
                        404));
    }

    public Optional<WorkflowStep> nextStep(int currentOrdinal) {
        return steps.stream().filter(step -> step.ordinal() == currentOrdinal + 1).findFirst();
    }

    public boolean isLastStep(int ordinal) {
        return steps.stream().mapToInt(WorkflowStep::ordinal).max().orElse(-1) == ordinal;
    }

    public List<WorkflowDefinitionDomainEvent> pullDomainEvents() {
        List<WorkflowDefinitionDomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    private static void validateSteps(List<WorkflowStep> steps) {
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("workflow must have at least one step");
        }
        if (steps.size() > 50) {
            throw new IllegalArgumentException("workflow cannot exceed 50 steps");
        }
        Set<String> keys = new HashSet<>();
        Set<Integer> ordinals = new HashSet<>();
        for (int i = 0; i < steps.size(); i++) {
            WorkflowStep step = Objects.requireNonNull(steps.get(i), "step");
            if (step.ordinal() != i) {
                throw new IllegalArgumentException("step ordinals must be contiguous starting at 0");
            }
            if (!keys.add(step.stepKey())) {
                throw new IllegalArgumentException("duplicate stepKey: " + step.stepKey());
            }
            if (!ordinals.add(step.ordinal())) {
                throw new IllegalArgumentException("duplicate ordinal: " + step.ordinal());
            }
        }
    }

    private static String requireName(String name) {
        Objects.requireNonNull(name, "name");
        String normalized = name.trim().replaceAll("\\s+", " ");
        if (normalized.length() < 2 || normalized.length() > 200) {
            throw new IllegalArgumentException("name length must be between 2 and 200");
        }
        return normalized;
    }

    private static String normalizeOptional(String value, int max) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() > max) {
            throw new IllegalArgumentException("description must be <= " + max + " characters");
        }
        return normalized;
    }

    private void touch(Instant now) {
        this.updatedAt = now;
    }

    private void record(WorkflowDefinitionDomainEvent event) {
        domainEvents.add(event);
    }

    public WorkflowDefinitionId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public DefinitionKey key() {
        return key;
    }

    public String name() {
        return name;
    }

    public Optional<String> description() {
        return Optional.ofNullable(description);
    }

    public int version() {
        return version;
    }

    public WorkflowDefinitionStatus status() {
        return status;
    }

    public List<WorkflowStep> steps() {
        return steps;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public long entityVersion() {
        return entityVersion;
    }
}
