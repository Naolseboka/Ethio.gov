package et.gov.ethgov.directory.domain.regionnode;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Regionnode. */
public final class RegionnodeId {
    private final UUID value;

    private RegionnodeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RegionnodeId newId() {
        return new RegionnodeId(UUID.randomUUID());
    }

    public static RegionnodeId of(UUID value) {
        return new RegionnodeId(value);
    }

    public static RegionnodeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RegionnodeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegionnodeId other)) {
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
