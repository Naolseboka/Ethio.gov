package et.gov.ethgov.gateway.domain.rewrite;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RewriteClosed. */
public final class RewriteClosed {
    private final RewriteId id;
    private final Instant occurredAt;

    public RewriteClosed(RewriteId id, Instant occurredAt) {
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
