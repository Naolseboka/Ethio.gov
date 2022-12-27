package et.gov.ethgov.notification.domain.message;

import java.util.Objects;
import java.util.UUID;

public final class NotificationId {
    private final String value;
    private NotificationId(String value) { this.value = value; }
    public static NotificationId generate() { return new NotificationId("ntf_" + UUID.randomUUID()); }
    public static NotificationId of(String raw) {
        Objects.requireNonNull(raw, "notificationId");
        String n = raw.trim();
        if (!n.startsWith("ntf_") || n.length() < 40) {
            throw new IllegalArgumentException("Invalid NotificationId: " + raw);
        }
        return new NotificationId(n);
    }
    public String value() { return value; }
    @Override public boolean equals(Object o) {
        return this == o || (o instanceof NotificationId that && value.equals(that.value));
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
