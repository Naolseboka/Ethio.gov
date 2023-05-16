package et.gov.ethgov.opendata.domain.harvest;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Harvest. */
public final class HarvestId {
    private final UUID value;

    private HarvestId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HarvestId newId() {
        return new HarvestId(UUID.randomUUID());
    }

    public static HarvestId of(UUID value) {
        return new HarvestId(value);
    }

    public static HarvestId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HarvestId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HarvestId other)) {
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
