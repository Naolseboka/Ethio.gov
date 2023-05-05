package et.gov.ethgov.opendata.domain.dataupdate;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Dataupdate. */
public final class DataupdateId {
    private final UUID value;

    private DataupdateId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DataupdateId newId() {
        return new DataupdateId(UUID.randomUUID());
    }

    public static DataupdateId of(UUID value) {
        return new DataupdateId(value);
    }

    public static DataupdateId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DataupdateId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DataupdateId other)) {
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
