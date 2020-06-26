package et.gov.ethgov.directory.domain.ward;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Ward. */
public final class WardId {
    private final UUID value;

    private WardId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static WardId newId() {
        return new WardId(UUID.randomUUID());
    }

    public static WardId of(UUID value) {
        return new WardId(value);
    }

    public static WardId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new WardId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WardId other)) {
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
