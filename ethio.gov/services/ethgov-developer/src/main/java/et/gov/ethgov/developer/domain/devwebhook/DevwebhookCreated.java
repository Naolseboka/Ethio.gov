package et.gov.ethgov.developer.domain.devwebhook;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DevwebhookCreated. */
public final class DevwebhookCreated {
    private final DevwebhookId id;
    private final Instant occurredAt;

    public DevwebhookCreated(DevwebhookId id, Instant occurredAt) {
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
