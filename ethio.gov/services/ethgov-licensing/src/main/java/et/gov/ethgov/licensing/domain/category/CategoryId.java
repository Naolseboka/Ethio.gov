package et.gov.ethgov.licensing.domain.category;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Category. */
public final class CategoryId {
    private final UUID value;

    private CategoryId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CategoryId newId() {
        return new CategoryId(UUID.randomUUID());
    }

    public static CategoryId of(UUID value) {
        return new CategoryId(value);
    }

    public static CategoryId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CategoryId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CategoryId other)) {
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
