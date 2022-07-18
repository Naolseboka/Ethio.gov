package et.gov.ethgov.licensing.domain.application;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Application. */
public final class ApplicationId {
    private final UUID value;

    private ApplicationId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ApplicationId newId() {
        return new ApplicationId(UUID.randomUUID());
    }

    public static ApplicationId of(UUID value) {
        return new ApplicationId(value);
    }

    public static ApplicationId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ApplicationId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApplicationId other)) {
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
