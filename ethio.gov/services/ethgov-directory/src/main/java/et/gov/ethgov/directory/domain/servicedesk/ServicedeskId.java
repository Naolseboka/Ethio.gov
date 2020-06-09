package et.gov.ethgov.directory.domain.servicedesk;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Servicedesk. */
public final class ServicedeskId {
    private final UUID value;

    private ServicedeskId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ServicedeskId newId() {
        return new ServicedeskId(UUID.randomUUID());
    }

    public static ServicedeskId of(UUID value) {
        return new ServicedeskId(value);
    }

    public static ServicedeskId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ServicedeskId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServicedeskId other)) {
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
