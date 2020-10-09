package et.gov.ethgov.document.domain.redaction;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Redaction. */
public final class RedactionId {
    private final UUID value;

    private RedactionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RedactionId newId() {
        return new RedactionId(UUID.randomUUID());
    }

    public static RedactionId of(UUID value) {
        return new RedactionId(value);
    }

    public static RedactionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RedactionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RedactionId other)) {
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
