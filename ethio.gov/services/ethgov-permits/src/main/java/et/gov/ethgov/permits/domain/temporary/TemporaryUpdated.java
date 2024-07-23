package et.gov.ethgov.permits.domain.temporary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TemporaryUpdated. */
public final class TemporaryUpdated {
    private final TemporaryId id;
    private final Instant occurredAt;

    public TemporaryUpdated(TemporaryId id, Instant occurredAt) {
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
