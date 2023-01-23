package et.gov.ethgov.notification.domain.subscription;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Subscription. */
public final class SubscriptionId {
    private final UUID value;

    private SubscriptionId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static SubscriptionId newId() {
        return new SubscriptionId(UUID.randomUUID());
    }

    public static SubscriptionId of(UUID value) {
        return new SubscriptionId(value);
    }

    public static SubscriptionId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new SubscriptionId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionId other)) {
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
