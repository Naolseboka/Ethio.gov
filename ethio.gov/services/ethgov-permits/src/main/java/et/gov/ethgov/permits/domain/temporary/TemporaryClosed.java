package et.gov.ethgov.permits.domain.temporary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TemporaryClosed. */
public final class TemporaryClosed {
    private final TemporaryId id;
    private final Instant occurredAt;

    public TemporaryClosed(TemporaryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TemporaryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
