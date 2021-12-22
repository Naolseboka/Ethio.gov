package et.gov.ethgov.identity.domain.riskscore;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Riskscore. */
public final class RiskscoreId {
    private final UUID value;

    private RiskscoreId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RiskscoreId newId() {
        return new RiskscoreId(UUID.randomUUID());
    }

    public static RiskscoreId of(UUID value) {
        return new RiskscoreId(value);
    }

    public static RiskscoreId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RiskscoreId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RiskscoreId other)) {
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
