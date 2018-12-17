package et.gov.ethgov.analytics.domain.scorecard;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Scorecard. */
public final class ScorecardId {
    private final UUID value;

    private ScorecardId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ScorecardId newId() {
        return new ScorecardId(UUID.randomUUID());
    }

    public static ScorecardId of(UUID value) {
        return new ScorecardId(value);
    }

    public static ScorecardId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ScorecardId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScorecardId other)) {
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
