package et.gov.ethgov.search.domain.facet;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Facet. */
public final class FacetId {
    private final UUID value;

    private FacetId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static FacetId newId() {
        return new FacetId(UUID.randomUUID());
    }

    public static FacetId of(UUID value) {
        return new FacetId(value);
    }

    public static FacetId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new FacetId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FacetId other)) {
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
