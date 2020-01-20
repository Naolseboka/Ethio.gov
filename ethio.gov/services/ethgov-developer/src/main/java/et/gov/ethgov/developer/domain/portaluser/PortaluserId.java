package et.gov.ethgov.developer.domain.portaluser;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Portaluser. */
public final class PortaluserId {
    private final UUID value;

    private PortaluserId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PortaluserId newId() {
        return new PortaluserId(UUID.randomUUID());
    }

    public static PortaluserId of(UUID value) {
        return new PortaluserId(value);
    }

    public static PortaluserId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PortaluserId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PortaluserId other)) {
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
