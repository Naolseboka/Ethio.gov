package et.gov.ethgov.gateway.domain.plugin;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PluginCreated. */
public final class PluginCreated {
    private final PluginId id;
    private final Instant occurredAt;

    public PluginCreated(PluginId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PluginId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
