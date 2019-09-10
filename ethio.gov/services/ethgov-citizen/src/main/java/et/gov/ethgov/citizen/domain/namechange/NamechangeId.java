package et.gov.ethgov.citizen.domain.namechange;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Namechange. */
public final class NamechangeId {
    private final UUID value;

    private NamechangeId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static NamechangeId newId() {
        return new NamechangeId(UUID.randomUUID());
    }

    public static NamechangeId of(UUID value) {
        return new NamechangeId(value);
    }

    public static NamechangeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new NamechangeId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NamechangeId other)) {
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
