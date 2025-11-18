package et.gov.ethgov.workflow.domain.checklist;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Checklist. */
public final class ChecklistId {
    private final UUID value;

    private ChecklistId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ChecklistId newId() {
        return new ChecklistId(UUID.randomUUID());
    }

    public static ChecklistId of(UUID value) {
        return new ChecklistId(value);
    }

    public static ChecklistId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ChecklistId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChecklistId other)) {
            return false;
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
