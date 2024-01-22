package et.gov.ethgov.payments.domain.cutoff;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Cutoff. */
public final class CutoffId {
    private final UUID value;

    private CutoffId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CutoffId newId() {
        return new CutoffId(UUID.randomUUID());
    }

    public static CutoffId of(UUID value) {
        return new CutoffId(value);
    }

    public static CutoffId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CutoffId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CutoffId other)) {
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
