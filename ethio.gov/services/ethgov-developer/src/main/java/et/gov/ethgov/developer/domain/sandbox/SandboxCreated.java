package et.gov.ethgov.developer.domain.sandbox;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SandboxCreated. */
public final class SandboxCreated {
    private final SandboxId id;
    private final Instant occurredAt;

    public SandboxCreated(SandboxId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SandboxId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
