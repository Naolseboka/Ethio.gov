package et.gov.ethgov.document.domain.scanbatch;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Scanbatch. */
public final class ScanbatchId {
    private final UUID value;

    private ScanbatchId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ScanbatchId newId() {
        return new ScanbatchId(UUID.randomUUID());
    }

    public static ScanbatchId of(UUID value) {
        return new ScanbatchId(value);
    }

    public static ScanbatchId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ScanbatchId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScanbatchId other)) {
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
