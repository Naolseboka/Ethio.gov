package et.gov.ethgov.notification.domain.notifywebhook;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Notifywebhook. */
public final class NotifywebhookId {
    private final UUID value;

    private NotifywebhookId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static NotifywebhookId newId() {
        return new NotifywebhookId(UUID.randomUUID());
    }

    public static NotifywebhookId of(UUID value) {
        return new NotifywebhookId(value);
    }

    public static NotifywebhookId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new NotifywebhookId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotifywebhookId other)) {
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
