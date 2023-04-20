package et.gov.ethgov.opendata.domain.datalicense;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Datalicense. */
public final class DatalicenseId {
    private final UUID value;

    private DatalicenseId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DatalicenseId newId() {
        return new DatalicenseId(UUID.randomUUID());
    }

    public static DatalicenseId of(UUID value) {
        return new DatalicenseId(value);
    }

    public static DatalicenseId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DatalicenseId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DatalicenseId other)) {
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
