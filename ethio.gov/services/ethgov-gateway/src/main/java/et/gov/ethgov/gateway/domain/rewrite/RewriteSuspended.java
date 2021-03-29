package et.gov.ethgov.gateway.domain.rewrite;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RewriteSuspended. */
public final class RewriteSuspended {
    private final RewriteId id;
    private final Instant occurredAt;

    public RewriteSuspended(RewriteId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RewriteId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
