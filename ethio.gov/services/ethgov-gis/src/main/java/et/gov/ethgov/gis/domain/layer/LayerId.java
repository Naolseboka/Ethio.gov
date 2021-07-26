package et.gov.ethgov.gis.domain.layer;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Layer. */
public final class LayerId {
    private final UUID value;

    private LayerId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static LayerId newId() {
        return new LayerId(UUID.randomUUID());
    }

    public static LayerId of(UUID value) {
        return new LayerId(value);
    }

    public static LayerId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new LayerId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LayerId other)) {
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
