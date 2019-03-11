package et.gov.ethgov.case.domain.casefile;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Casefile. */
public final class CasefileId {
    private final UUID value;

    private CasefileId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CasefileId newId() {
        return new CasefileId(UUID.randomUUID());
    }

    public static CasefileId of(UUID value) {
        return new CasefileId(value);
    }

    public static CasefileId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CasefileId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CasefileId other)) {
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
