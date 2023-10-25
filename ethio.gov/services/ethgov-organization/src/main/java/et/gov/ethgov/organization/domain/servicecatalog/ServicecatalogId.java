package et.gov.ethgov.organization.domain.servicecatalog;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Servicecatalog. */
public final class ServicecatalogId {
    private final UUID value;

    private ServicecatalogId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ServicecatalogId newId() {
        return new ServicecatalogId(UUID.randomUUID());
    }

    public static ServicecatalogId of(UUID value) {
        return new ServicecatalogId(value);
    }

    public static ServicecatalogId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ServicecatalogId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicecatalogId other)) {
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
