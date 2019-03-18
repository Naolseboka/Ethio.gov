package et.gov.ethgov.case.domain.casenote;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Casenote. */
public final class CasenoteId {
    private final UUID value;

    private CasenoteId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CasenoteId newId() {
        return new CasenoteId(UUID.randomUUID());
    }

    public static CasenoteId of(UUID value) {
        return new CasenoteId(value);
    }

    public static CasenoteId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CasenoteId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CasenoteId other)) {
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
