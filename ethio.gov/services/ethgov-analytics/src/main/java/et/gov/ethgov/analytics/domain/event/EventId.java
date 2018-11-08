package et.gov.ethgov.analytics.domain.event;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Event. */
public final class EventId {
    private final UUID value;

    private EventId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static EventId newId() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId of(UUID value) {
        return new EventId(value);
    }

    public static EventId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new EventId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventId other)) {
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
