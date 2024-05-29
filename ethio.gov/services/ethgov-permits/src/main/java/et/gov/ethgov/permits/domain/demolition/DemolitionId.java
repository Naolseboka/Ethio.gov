package et.gov.ethgov.permits.domain.demolition;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Demolition. */
public final class DemolitionId {
    private final UUID value;

    private DemolitionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DemolitionId newId() {
        return new DemolitionId(UUID.randomUUID());
    }

    public static DemolitionId of(UUID value) {
        return new DemolitionId(value);
    }

    public static DemolitionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DemolitionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemolitionId other)) {
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
