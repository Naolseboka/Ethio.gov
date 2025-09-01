package et.gov.ethgov.search.domain.synonym;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Synonym. */
public final class SynonymId {
    private final UUID value;

    private SynonymId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SynonymId newId() {
        return new SynonymId(UUID.randomUUID());
    }

    public static SynonymId of(UUID value) {
        return new SynonymId(value);
    }

    public static SynonymId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SynonymId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SynonymId other)) {
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
