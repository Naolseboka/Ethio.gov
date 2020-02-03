package et.gov.ethgov.developer.domain.supportticket;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Supportticket. */
public final class SupportticketId {
    private final UUID value;

    private SupportticketId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SupportticketId newId() {
        return new SupportticketId(UUID.randomUUID());
    }

    public static SupportticketId of(UUID value) {
        return new SupportticketId(value);
    }

    public static SupportticketId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SupportticketId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SupportticketId other)) {
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
