package et.gov.ethgov.revenue.domain.penalty;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Penalty. */
public final class PenaltyId {
    private final UUID value;

    private PenaltyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PenaltyId newId() {
        return new PenaltyId(UUID.randomUUID());
    }

    public static PenaltyId of(UUID value) {
        return new PenaltyId(value);
    }

    public static PenaltyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PenaltyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PenaltyId other)) {
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
