package et.gov.ethgov.developer.domain.quota;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QuotaUpdated. */
public final class QuotaUpdated {
    private final QuotaId id;
    private final Instant occurredAt;

    public QuotaUpdated(QuotaId id, Instant occurredAt) {
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
