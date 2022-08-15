package et.gov.ethgov.licensing.domain.holder;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Holder. */
public final class HolderId {
    private final UUID value;

    private HolderId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static HolderId newId() {
        return new HolderId(UUID.randomUUID());
    }

    public static HolderId of(UUID value) {
        return new HolderId(value);
    }

    public static HolderId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new HolderId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HolderId other)) {
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
