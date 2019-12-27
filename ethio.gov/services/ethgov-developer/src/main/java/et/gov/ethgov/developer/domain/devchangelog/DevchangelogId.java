package et.gov.ethgov.developer.domain.devchangelog;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Devchangelog. */
public final class DevchangelogId {
    private final UUID value;

    private DevchangelogId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DevchangelogId newId() {
        return new DevchangelogId(UUID.randomUUID());
    }

    public static DevchangelogId of(UUID value) {
        return new DevchangelogId(value);
    }

    public static DevchangelogId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DevchangelogId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevchangelogId other)) {
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
