package et.gov.ethgov.licensing.domain.endorsement;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Endorsement. */
public final class EndorsementId {
    private final UUID value;

    private EndorsementId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EndorsementId newId() {
        return new EndorsementId(UUID.randomUUID());
    }

    public static EndorsementId of(UUID value) {
        return new EndorsementId(value);
    }

    public static EndorsementId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EndorsementId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EndorsementId other)) {
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
