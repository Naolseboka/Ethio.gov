package et.gov.ethgov.analytics.domain.experiment;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Experiment. */
public final class ExperimentId {
    private final UUID value;

    private ExperimentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ExperimentId newId() {
        return new ExperimentId(UUID.randomUUID());
    }

    public static ExperimentId of(UUID value) {
        return new ExperimentId(value);
    }

    public static ExperimentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ExperimentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExperimentId other)) {
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
