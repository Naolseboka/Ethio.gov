package et.gov.ethgov.case.domain.caseassignment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Caseassignment. */
public final class CaseassignmentId {
    private final UUID value;

    private CaseassignmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CaseassignmentId newId() {
        return new CaseassignmentId(UUID.randomUUID());
    }

    public static CaseassignmentId of(UUID value) {
        return new CaseassignmentId(value);
    }

    public static CaseassignmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CaseassignmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CaseassignmentId other)) {
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
