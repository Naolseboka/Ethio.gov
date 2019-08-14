package et.gov.ethgov.citizen.domain.documentlink;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Documentlink. */
public final class DocumentlinkId {
    private final UUID value;

    private DocumentlinkId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DocumentlinkId newId() {
        return new DocumentlinkId(UUID.randomUUID());
    }

    public static DocumentlinkId of(UUID value) {
        return new DocumentlinkId(value);
    }

    public static DocumentlinkId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DocumentlinkId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DocumentlinkId other)) {
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
