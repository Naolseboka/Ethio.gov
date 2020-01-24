package et.gov.ethgov.developer.domain.quota;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QuotaCreated. */
public final class QuotaCreated {
    private final QuotaId id;
    private final Instant occurredAt;

    public QuotaCreated(QuotaId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public QuotaId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
