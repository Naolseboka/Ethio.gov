package et.gov.ethgov.analytics.domain.cohort;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Cohort. */
public final class CohortId {
    private final UUID value;

    private CohortId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CohortId newId() {
        return new CohortId(UUID.randomUUID());
    }

    public static CohortId of(UUID value) {
        return new CohortId(value);
    }

    public static CohortId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CohortId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CohortId other)) {
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
