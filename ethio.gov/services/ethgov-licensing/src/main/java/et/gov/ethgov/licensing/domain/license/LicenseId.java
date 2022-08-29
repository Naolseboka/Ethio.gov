package et.gov.ethgov.licensing.domain.license;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for License. */
public final class LicenseId {
    private final UUID value;

    private LicenseId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LicenseId newId() {
        return new LicenseId(UUID.randomUUID());
    }

    public static LicenseId of(UUID value) {
        return new LicenseId(value);
    }

    public static LicenseId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LicenseId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LicenseId other)) {
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
