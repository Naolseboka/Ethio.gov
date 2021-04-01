package et.gov.ethgov.gateway.domain.route;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Route. */
public final class RouteId {
    private final UUID value;

    private RouteId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RouteId newId() {
        return new RouteId(UUID.randomUUID());
    }

    public static RouteId of(UUID value) {
        return new RouteId(value);
    }

    public static RouteId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RouteId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RouteId other)) {
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
