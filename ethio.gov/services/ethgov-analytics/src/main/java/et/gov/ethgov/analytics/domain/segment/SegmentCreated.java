package et.gov.ethgov.analytics.domain.segment;

import java.time.Instant;
import java.util.Objects;

/** Domain event: SegmentCreated. */
public final class SegmentCreated {
    private final SegmentId id;
    private final Instant occurredAt;

    public SegmentCreated(SegmentId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public SegmentId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
