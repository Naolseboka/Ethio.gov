package et.gov.ethgov.notification.domain.notifychannel;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Notifychannel. */
public final class NotifychannelId {
    private final UUID value;

    private NotifychannelId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static NotifychannelId newId() {
        return new NotifychannelId(UUID.randomUUID());
    }

    public static NotifychannelId of(UUID value) {
        return new NotifychannelId(value);
    }

    public static NotifychannelId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new NotifychannelId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotifychannelId other)) {
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
