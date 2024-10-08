package et.gov.ethgov.reporting.domain.dataset;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Dataset. */
public final class DatasetId {
    private final UUID value;

    private DatasetId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DatasetId newId() {
        return new DatasetId(UUID.randomUUID());
    }

    public static DatasetId of(UUID value) {
        return new DatasetId(value);
    }

    public static DatasetId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DatasetId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatasetId other)) {
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
