package et.gov.ethgov.document.domain.accessgrant;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Accessgrant. */
public final class AccessgrantId {
    private final UUID value;

    private AccessgrantId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static AccessgrantId newId() {
        return new AccessgrantId(UUID.randomUUID());
    }

    public static AccessgrantId of(UUID value) {
        return new AccessgrantId(value);
    }

    public static AccessgrantId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new AccessgrantId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccessgrantId other)) {
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
