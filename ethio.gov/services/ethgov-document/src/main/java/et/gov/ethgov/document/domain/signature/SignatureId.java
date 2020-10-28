package et.gov.ethgov.document.domain.signature;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Signature. */
public final class SignatureId {
    private final UUID value;

    private SignatureId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SignatureId newId() {
        return new SignatureId(UUID.randomUUID());
    }

    public static SignatureId of(UUID value) {
        return new SignatureId(value);
    }

    public static SignatureId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SignatureId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SignatureId other)) {
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
