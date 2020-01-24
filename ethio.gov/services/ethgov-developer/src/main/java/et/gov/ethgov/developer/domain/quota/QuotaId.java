package et.gov.ethgov.developer.domain.quota;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Quota. */
public final class QuotaId {
    private final UUID value;

    private QuotaId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static QuotaId newId() {
        return new QuotaId(UUID.randomUUID());
    }

    public static QuotaId of(UUID value) {
        return new QuotaId(value);
    }

    public static QuotaId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new QuotaId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuotaId other)) {
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
