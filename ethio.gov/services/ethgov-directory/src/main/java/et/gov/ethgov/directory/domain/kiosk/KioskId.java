package et.gov.ethgov.directory.domain.kiosk;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Kiosk. */
public final class KioskId {
    private final UUID value;

    private KioskId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static KioskId newId() {
        return new KioskId(UUID.randomUUID());
    }

    public static KioskId of(UUID value) {
        return new KioskId(value);
    }

    public static KioskId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new KioskId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KioskId other)) {
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
