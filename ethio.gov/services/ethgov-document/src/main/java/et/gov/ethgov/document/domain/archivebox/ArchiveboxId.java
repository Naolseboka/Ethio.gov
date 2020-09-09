package et.gov.ethgov.document.domain.archivebox;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Archivebox. */
public final class ArchiveboxId {
    private final UUID value;

    private ArchiveboxId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ArchiveboxId newId() {
        return new ArchiveboxId(UUID.randomUUID());
    }

    public static ArchiveboxId of(UUID value) {
        return new ArchiveboxId(value);
    }

    public static ArchiveboxId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ArchiveboxId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArchiveboxId other)) {
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
