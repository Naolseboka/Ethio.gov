package et.gov.ethgov.identity.domain.consent;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Consent. */
public final class ConsentId {
    private final UUID value;

    private ConsentId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ConsentId newId() {
        return new ConsentId(UUID.randomUUID());
    }

    public static ConsentId of(UUID value) {
        return new ConsentId(value);
    }

    public static ConsentId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ConsentId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsentId other)) {
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
