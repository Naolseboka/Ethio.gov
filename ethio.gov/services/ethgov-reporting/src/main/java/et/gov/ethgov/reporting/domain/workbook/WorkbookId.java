package et.gov.ethgov.reporting.domain.workbook;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Workbook. */
public final class WorkbookId {
    private final UUID value;

    private WorkbookId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WorkbookId newId() {
        return new WorkbookId(UUID.randomUUID());
    }

    public static WorkbookId of(UUID value) {
        return new WorkbookId(value);
    }

    public static WorkbookId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WorkbookId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkbookId other)) {
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
