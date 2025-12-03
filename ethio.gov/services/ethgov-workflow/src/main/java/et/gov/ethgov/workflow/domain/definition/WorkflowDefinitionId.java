package et.gov.ethgov.workflow.domain.definition;

import java.util.Objects;
import java.util.UUID;

public final class WorkflowDefinitionId {

    private final String value;

    private WorkflowDefinitionId(String value) {
        this.value = value;
    }

    public static WorkflowDefinitionId generate() {
        return new WorkflowDefinitionId("wfd_" + UUID.randomUUID());
    }

    public static WorkflowDefinitionId of(String raw) {
        Objects.requireNonNull(raw, "workflowDefinitionId");
        String normalized = raw.trim();
        if (!normalized.startsWith("wfd_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid WorkflowDefinitionId: " + raw);
        }
        return new WorkflowDefinitionId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WorkflowDefinitionId that)) {
            return false;
        }
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
