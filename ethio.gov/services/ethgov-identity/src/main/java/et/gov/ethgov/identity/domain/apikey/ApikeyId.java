package et.gov.ethgov.identity.domain.apikey;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Apikey. */
public final class ApikeyId {
    private final UUID value;

    private ApikeyId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ApikeyId newId() {
        return new ApikeyId(UUID.randomUUID());
    }

    public static ApikeyId of(UUID value) {
        return new ApikeyId(value);
    }

    public static ApikeyId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ApikeyId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApikeyId other)) {
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
