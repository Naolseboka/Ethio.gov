package et.gov.ethgov.workflow.domain.sla;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Sla. */
public final class SlaId {
    private final UUID value;

    private SlaId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SlaId newId() {
        return new SlaId(UUID.randomUUID());
    }

    public static SlaId of(UUID value) {
        return new SlaId(value);
    }

    public static SlaId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SlaId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SlaId other)) {
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
