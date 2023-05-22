package et.gov.ethgov.opendata.domain.opendataset;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Opendataset. */
public final class OpendatasetId {
    private final UUID value;

    private OpendatasetId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OpendatasetId newId() {
        return new OpendatasetId(UUID.randomUUID());
    }

    public static OpendatasetId of(UUID value) {
        return new OpendatasetId(value);
    }

    public static OpendatasetId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OpendatasetId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpendatasetId other)) {
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
