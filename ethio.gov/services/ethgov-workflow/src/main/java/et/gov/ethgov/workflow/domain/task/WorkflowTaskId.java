package et.gov.ethgov.workflow.domain.task;

import java.util.Objects;
import java.util.UUID;

public final class WorkflowTaskId {

    private final String value;

    private WorkflowTaskId(String value) {
        this.value = value;
    }

    public static WorkflowTaskId generate() {
        return new WorkflowTaskId("wtk_" + UUID.randomUUID());
    }

    public static WorkflowTaskId of(String raw) {
        Objects.requireNonNull(raw, "workflowTaskId");
        String normalized = raw.trim();
        if (!normalized.startsWith("wtk_") || normalized.length() < 40) {
            throw new IllegalArgumentException("Invalid WorkflowTaskId: " + raw);
        }
        return new WorkflowTaskId(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WorkflowTaskId that)) {
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
