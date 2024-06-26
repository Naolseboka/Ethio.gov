package et.gov.ethgov.permits.domain.permitapplication;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Permitapplication. */
public final class PermitapplicationId {
    private final UUID value;

    private PermitapplicationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PermitapplicationId newId() {
        return new PermitapplicationId(UUID.randomUUID());
    }

    public static PermitapplicationId of(UUID value) {
        return new PermitapplicationId(value);
    }

    public static PermitapplicationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PermitapplicationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermitapplicationId other)) {
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
