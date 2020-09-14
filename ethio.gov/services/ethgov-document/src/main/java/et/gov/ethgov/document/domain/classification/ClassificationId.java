package et.gov.ethgov.document.domain.classification;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Classification. */
public final class ClassificationId {
    private final UUID value;

    private ClassificationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ClassificationId newId() {
        return new ClassificationId(UUID.randomUUID());
    }

    public static ClassificationId of(UUID value) {
        return new ClassificationId(value);
    }

    public static ClassificationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ClassificationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassificationId other)) {
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
