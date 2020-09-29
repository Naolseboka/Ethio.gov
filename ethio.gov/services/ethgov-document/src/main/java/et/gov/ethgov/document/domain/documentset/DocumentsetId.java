package et.gov.ethgov.document.domain.documentset;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Documentset. */
public final class DocumentsetId {
    private final UUID value;

    private DocumentsetId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DocumentsetId newId() {
        return new DocumentsetId(UUID.randomUUID());
    }

    public static DocumentsetId of(UUID value) {
        return new DocumentsetId(value);
    }

    public static DocumentsetId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DocumentsetId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentsetId other)) {
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
