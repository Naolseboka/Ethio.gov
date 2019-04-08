package et.gov.ethgov.case.domain.hearing;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HearingSuspended. */
public final class HearingSuspended {
    private final HearingId id;
    private final Instant occurredAt;

    public HearingSuspended(HearingId id, Instant occurredAt) {
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
