package et.gov.ethgov.developer.domain.devwebhook;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevwebhookClosed. */
public final class DevwebhookClosed {
    private final DevwebhookId id;
    private final Instant occurredAt;

    public DevwebhookClosed(DevwebhookId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DevwebhookId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
