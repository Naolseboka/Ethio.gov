package et.gov.ethgov.licensing.domain.renewal;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Renewal. */
public final class RenewalId {
    private final UUID value;

    private RenewalId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RenewalId newId() {
        return new RenewalId(UUID.randomUUID());
    }

    public static RenewalId of(UUID value) {
        return new RenewalId(value);
    }

    public static RenewalId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RenewalId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenewalId other)) {
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
