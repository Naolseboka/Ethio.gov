package et.gov.ethgov.directory.domain.office;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Office. */
public final class OfficeId {
    private final UUID value;

    private OfficeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static OfficeId newId() {
        return new OfficeId(UUID.randomUUID());
    }

    public static OfficeId of(UUID value) {
        return new OfficeId(value);
    }

    public static OfficeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new OfficeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfficeId other)) {
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
