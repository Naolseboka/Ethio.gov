package et.gov.ethgov.gateway.domain.plugin;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PluginActivated. */
public final class PluginActivated {
    private final PluginId id;
    private final Instant occurredAt;

    public PluginActivated(PluginId id, Instant occurredAt) {
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
