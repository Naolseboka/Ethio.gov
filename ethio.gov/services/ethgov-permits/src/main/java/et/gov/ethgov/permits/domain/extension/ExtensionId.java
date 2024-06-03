package et.gov.ethgov.permits.domain.extension;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Extension. */
public final class ExtensionId {
    private final UUID value;

    private ExtensionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ExtensionId newId() {
        return new ExtensionId(UUID.randomUUID());
    }

    public static ExtensionId of(UUID value) {
        return new ExtensionId(value);
    }

    public static ExtensionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ExtensionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtensionId other)) {
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
