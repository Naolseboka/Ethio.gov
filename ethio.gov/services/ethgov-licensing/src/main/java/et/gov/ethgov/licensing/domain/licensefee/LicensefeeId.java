package et.gov.ethgov.licensing.domain.licensefee;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Licensefee. */
public final class LicensefeeId {
    private final UUID value;

    private LicensefeeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LicensefeeId newId() {
        return new LicensefeeId(UUID.randomUUID());
    }

    public static LicensefeeId of(UUID value) {
        return new LicensefeeId(value);
    }

    public static LicensefeeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LicensefeeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicensefeeId other)) {
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
