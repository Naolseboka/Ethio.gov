package et.gov.ethgov.directory.domain.openinghours;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Openinghours. */
public final class OpeninghoursId {
    private final UUID value;

    private OpeninghoursId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OpeninghoursId newId() {
        return new OpeninghoursId(UUID.randomUUID());
    }

    public static OpeninghoursId of(UUID value) {
        return new OpeninghoursId(value);
    }

    public static OpeninghoursId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OpeninghoursId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OpeninghoursId other)) {
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
