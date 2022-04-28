package et.gov.ethgov.land.domain.title;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Title. */
public final class TitleId {
    private final UUID value;

    private TitleId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static TitleId newId() {
        return new TitleId(UUID.randomUUID());
    }

    public static TitleId of(UUID value) {
        return new TitleId(value);
    }

    public static TitleId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new TitleId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TitleId other)) {
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
