package et.gov.ethgov.search.domain.index;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Index. */
public final class IndexId {
    private final UUID value;

    private IndexId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static IndexId newId() {
        return new IndexId(UUID.randomUUID());
    }

    public static IndexId of(UUID value) {
        return new IndexId(value);
    }

    public static IndexId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new IndexId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IndexId other)) {
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
