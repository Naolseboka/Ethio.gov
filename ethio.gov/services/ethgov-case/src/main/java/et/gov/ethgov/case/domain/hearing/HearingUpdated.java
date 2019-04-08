package et.gov.ethgov.case.domain.hearing;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HearingUpdated. */
public final class HearingUpdated {
    private final HearingId id;
    private final Instant occurredAt;

    public HearingUpdated(HearingId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HearingId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
