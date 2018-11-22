package et.gov.ethgov.analytics.domain.funnel;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Funnel. */
public final class FunnelId {
    private final UUID value;

    private FunnelId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static FunnelId newId() {
        return new FunnelId(UUID.randomUUID());
    }

    public static FunnelId of(UUID value) {
        return new FunnelId(value);
    }

    public static FunnelId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new FunnelId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FunnelId other)) {
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
