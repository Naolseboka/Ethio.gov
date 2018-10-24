package et.gov.ethgov.analytics.domain.attribution;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Attribution. */
public final class AttributionId {
    private final UUID value;

    private AttributionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AttributionId newId() {
        return new AttributionId(UUID.randomUUID());
    }

    public static AttributionId of(UUID value) {
        return new AttributionId(value);
    }

    public static AttributionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AttributionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AttributionId other)) {
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
