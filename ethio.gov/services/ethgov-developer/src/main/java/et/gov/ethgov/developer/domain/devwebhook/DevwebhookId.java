package et.gov.ethgov.developer.domain.devwebhook;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Devwebhook. */
public final class DevwebhookId {
    private final UUID value;

    private DevwebhookId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static DevwebhookId newId() {
        return new DevwebhookId(UUID.randomUUID());
    }

    public static DevwebhookId of(UUID value) {
        return new DevwebhookId(value);
    }

    public static DevwebhookId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new DevwebhookId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DevwebhookId other)) {
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
