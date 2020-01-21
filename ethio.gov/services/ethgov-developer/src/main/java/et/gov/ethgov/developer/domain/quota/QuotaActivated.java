package et.gov.ethgov.developer.domain.quota;

import java.time.Instant;
import java.util.Objects;

/** Domain event: QuotaActivated. */
public final class QuotaActivated {
    private final QuotaId id;
    private final Instant occurredAt;

    public QuotaActivated(QuotaId id, Instant occurredAt) {
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
