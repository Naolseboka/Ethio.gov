package et.gov.ethgov.opendata.domain.tag;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Tag. */
public final class TagId {
    private final UUID value;

    private TagId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TagId newId() {
        return new TagId(UUID.randomUUID());
    }

    public static TagId of(UUID value) {
        return new TagId(value);
    }

    public static TagId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TagId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TagId other)) {
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
