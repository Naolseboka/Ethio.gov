package et.gov.ethgov.revenue.domain.assessment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Assessment. */
public final class AssessmentId {
    private final UUID value;

    private AssessmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AssessmentId newId() {
        return new AssessmentId(UUID.randomUUID());
    }

    public static AssessmentId of(UUID value) {
        return new AssessmentId(value);
    }

    public static AssessmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AssessmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssessmentId other)) {
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
