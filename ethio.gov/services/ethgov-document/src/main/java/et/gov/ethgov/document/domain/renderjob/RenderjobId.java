package et.gov.ethgov.document.domain.renderjob;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Renderjob. */
public final class RenderjobId {
    private final UUID value;

    private RenderjobId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static RenderjobId newId() {
        return new RenderjobId(UUID.randomUUID());
    }

    public static RenderjobId of(UUID value) {
        return new RenderjobId(value);
    }

    public static RenderjobId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new RenderjobId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenderjobId other)) {
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
