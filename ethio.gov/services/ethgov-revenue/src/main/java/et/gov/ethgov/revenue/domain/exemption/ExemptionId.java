package et.gov.ethgov.revenue.domain.exemption;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Exemption. */
public final class ExemptionId {
    private final UUID value;

    private ExemptionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ExemptionId newId() {
        return new ExemptionId(UUID.randomUUID());
    }

    public static ExemptionId of(UUID value) {
        return new ExemptionId(value);
    }

    public static ExemptionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ExemptionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExemptionId other)) {
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
