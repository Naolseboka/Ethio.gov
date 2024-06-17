package et.gov.ethgov.permits.domain.permit;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Permit. */
public final class PermitId {
    private final UUID value;

    private PermitId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PermitId newId() {
        return new PermitId(UUID.randomUUID());
    }

    public static PermitId of(UUID value) {
        return new PermitId(value);
    }

    public static PermitId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PermitId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermitId other)) {
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
