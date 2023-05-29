package et.gov.ethgov.opendata.domain.publisher;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Publisher. */
public final class PublisherId {
    private final UUID value;

    private PublisherId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PublisherId newId() {
        return new PublisherId(UUID.randomUUID());
    }

    public static PublisherId of(UUID value) {
        return new PublisherId(value);
    }

    public static PublisherId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PublisherId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PublisherId other)) {
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
