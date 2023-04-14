package et.gov.ethgov.opendata.domain.catalog;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Catalog. */
public final class CatalogId {
    private final UUID value;

    private CatalogId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CatalogId newId() {
        return new CatalogId(UUID.randomUUID());
    }

    public static CatalogId of(UUID value) {
        return new CatalogId(value);
    }

    public static CatalogId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CatalogId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CatalogId other)) {
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
