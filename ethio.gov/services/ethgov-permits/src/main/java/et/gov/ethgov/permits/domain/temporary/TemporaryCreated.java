package et.gov.ethgov.permits.domain.temporary;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TemporaryCreated. */
public final class TemporaryCreated {
    private final TemporaryId id;
    private final Instant occurredAt;

    public TemporaryCreated(TemporaryId id, Instant occurredAt) {
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
