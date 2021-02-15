package et.gov.ethgov.gateway.domain.cors;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Cors. */
public final class CorsId {
    private final UUID value;

    private CorsId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CorsId newId() {
        return new CorsId(UUID.randomUUID());
    }

    public static CorsId of(UUID value) {
        return new CorsId(value);
    }

    public static CorsId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CorsId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CorsId other)) {
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
