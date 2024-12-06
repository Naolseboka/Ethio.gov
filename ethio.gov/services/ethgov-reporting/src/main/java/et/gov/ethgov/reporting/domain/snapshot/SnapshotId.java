package et.gov.ethgov.reporting.domain.snapshot;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Snapshot. */
public final class SnapshotId {
    private final UUID value;

    private SnapshotId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SnapshotId newId() {
        return new SnapshotId(UUID.randomUUID());
    }

    public static SnapshotId of(UUID value) {
        return new SnapshotId(value);
    }

    public static SnapshotId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SnapshotId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SnapshotId other)) {
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
