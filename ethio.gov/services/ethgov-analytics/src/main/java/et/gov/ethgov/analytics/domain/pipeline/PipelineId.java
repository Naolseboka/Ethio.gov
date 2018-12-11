package et.gov.ethgov.analytics.domain.pipeline;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Pipeline. */
public final class PipelineId {
    private final UUID value;

    private PipelineId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PipelineId newId() {
        return new PipelineId(UUID.randomUUID());
    }

    public static PipelineId of(UUID value) {
        return new PipelineId(value);
    }

    public static PipelineId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PipelineId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PipelineId other)) {
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
