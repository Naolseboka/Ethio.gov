package et.gov.ethgov.land.domain.cadastralmap;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Cadastralmap. */
public final class CadastralmapId {
    private final UUID value;

    private CadastralmapId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static CadastralmapId newId() {
        return new CadastralmapId(UUID.randomUUID());
    }

    public static CadastralmapId of(UUID value) {
        return new CadastralmapId(value);
    }

    public static CadastralmapId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new CadastralmapId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CadastralmapId other)) {
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
