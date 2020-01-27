package et.gov.ethgov.developer.domain.sandbox;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SandboxActivated. */
public final class SandboxActivated {
    private final SandboxId id;
    private final Instant occurredAt;

    public SandboxActivated(SandboxId id, Instant occurredAt) {
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
