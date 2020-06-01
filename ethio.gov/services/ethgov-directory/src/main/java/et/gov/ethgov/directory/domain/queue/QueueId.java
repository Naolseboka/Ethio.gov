package et.gov.ethgov.directory.domain.queue;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Queue. */
public final class QueueId {
    private final UUID value;

    private QueueId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static QueueId newId() {
        return new QueueId(UUID.randomUUID());
    }

    public static QueueId of(UUID value) {
        return new QueueId(value);
    }

    public static QueueId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new QueueId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueId other)) {
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
