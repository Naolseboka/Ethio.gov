package et.gov.ethgov.reporting.domain.schedule;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Schedule. */
public final class ScheduleId {
    private final UUID value;

    private ScheduleId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ScheduleId newId() {
        return new ScheduleId(UUID.randomUUID());
    }

    public static ScheduleId of(UUID value) {
        return new ScheduleId(value);
    }

    public static ScheduleId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ScheduleId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleId other)) {
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
