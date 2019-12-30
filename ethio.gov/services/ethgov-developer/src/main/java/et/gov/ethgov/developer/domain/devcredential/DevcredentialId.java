package et.gov.ethgov.developer.domain.devcredential;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Devcredential. */
public final class DevcredentialId {
    private final UUID value;

    private DevcredentialId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DevcredentialId newId() {
        return new DevcredentialId(UUID.randomUUID());
    }

    public static DevcredentialId of(UUID value) {
        return new DevcredentialId(value);
    }

    public static DevcredentialId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DevcredentialId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevcredentialId other)) {
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
