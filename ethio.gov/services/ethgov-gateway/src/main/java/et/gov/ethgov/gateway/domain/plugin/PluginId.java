package et.gov.ethgov.gateway.domain.plugin;

import java.util.Objects;
import java.util.UUID;

/** Strongly-typed identifier for Plugin. */
public final class PluginId {
    private final UUID value;

    private PluginId(UUID value) {
        this.value = Objects.requireNonNull(value, "value");
    }

    public static PluginId newId() {
        return new PluginId(UUID.randomUUID());
    }

    public static PluginId of(UUID value) {
        return new PluginId(value);
    }

    public static PluginId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        return new PluginId(UUID.fromString(raw.trim()));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PluginId other)) {
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
