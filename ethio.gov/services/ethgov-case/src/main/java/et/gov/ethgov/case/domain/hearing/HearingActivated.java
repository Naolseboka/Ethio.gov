package et.gov.ethgov.case.domain.hearing;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HearingActivated. */
public final class HearingActivated {
    private final HearingId id;
    private final Instant occurredAt;

    public HearingActivated(HearingId id, Instant occurredAt) {
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
