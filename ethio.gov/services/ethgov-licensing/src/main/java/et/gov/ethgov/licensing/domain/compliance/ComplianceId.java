package et.gov.ethgov.licensing.domain.compliance;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Compliance. */
public final class ComplianceId {
    private final UUID value;

    private ComplianceId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ComplianceId newId() {
        return new ComplianceId(UUID.randomUUID());
    }

    public static ComplianceId of(UUID value) {
        return new ComplianceId(value);
    }

    public static ComplianceId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ComplianceId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComplianceId other)) {
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
