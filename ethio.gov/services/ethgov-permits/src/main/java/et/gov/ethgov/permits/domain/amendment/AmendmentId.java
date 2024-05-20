package et.gov.ethgov.permits.domain.amendment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Amendment. */
public final class AmendmentId {
    private final UUID value;

    private AmendmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AmendmentId newId() {
        return new AmendmentId(UUID.randomUUID());
    }

    public static AmendmentId of(UUID value) {
        return new AmendmentId(value);
    }

    public static AmendmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AmendmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmendmentId other)) {
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
