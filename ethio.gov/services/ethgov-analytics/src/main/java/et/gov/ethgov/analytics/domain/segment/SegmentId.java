package et.gov.ethgov.analytics.domain.segment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Segment. */
public final class SegmentId {
    private final UUID value;

    private SegmentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SegmentId newId() {
        return new SegmentId(UUID.randomUUID());
    }

    public static SegmentId of(UUID value) {
        return new SegmentId(value);
    }

    public static SegmentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SegmentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SegmentId other)) {
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
