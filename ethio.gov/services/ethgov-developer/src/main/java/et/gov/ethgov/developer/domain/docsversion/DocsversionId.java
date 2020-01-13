package et.gov.ethgov.developer.domain.docsversion;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Docsversion. */
public final class DocsversionId {
    private final UUID value;

    private DocsversionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DocsversionId newId() {
        return new DocsversionId(UUID.randomUUID());
    }

    public static DocsversionId of(UUID value) {
        return new DocsversionId(value);
    }

    public static DocsversionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DocsversionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocsversionId other)) {
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
