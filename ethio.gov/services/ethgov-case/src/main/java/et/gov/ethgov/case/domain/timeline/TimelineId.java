package et.gov.ethgov.case.domain.timeline;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Timeline. */
public final class TimelineId {
    private final UUID value;

    private TimelineId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TimelineId newId() {
        return new TimelineId(UUID.randomUUID());
    }

    public static TimelineId of(UUID value) {
        return new TimelineId(value);
    }

    public static TimelineId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TimelineId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimelineId other)) {
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
