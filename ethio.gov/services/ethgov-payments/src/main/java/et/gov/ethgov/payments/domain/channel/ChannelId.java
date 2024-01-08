package et.gov.ethgov.payments.domain.channel;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Channel. */
public final class ChannelId {
    private final UUID value;

    private ChannelId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static ChannelId newId() {
        return new ChannelId(UUID.randomUUID());
    }

    public static ChannelId of(UUID value) {
        return new ChannelId(value);
    }

    public static ChannelId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new ChannelId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChannelId other)) {
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
