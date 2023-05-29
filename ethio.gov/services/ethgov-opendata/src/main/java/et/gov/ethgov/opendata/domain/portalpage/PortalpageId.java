package et.gov.ethgov.opendata.domain.portalpage;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Portalpage. */
public final class PortalpageId {
    private final UUID value;

    private PortalpageId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PortalpageId newId() {
        return new PortalpageId(UUID.randomUUID());
    }

    public static PortalpageId of(UUID value) {
        return new PortalpageId(value);
    }

    public static PortalpageId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PortalpageId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PortalpageId other)) {
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
