package et.gov.ethgov.analytics.domain.forecast;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Forecast. */
public final class ForecastId {
    private final UUID value;

    private ForecastId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ForecastId newId() {
        return new ForecastId(UUID.randomUUID());
    }

    public static ForecastId of(UUID value) {
        return new ForecastId(value);
    }

    public static ForecastId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ForecastId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForecastId other)) {
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
