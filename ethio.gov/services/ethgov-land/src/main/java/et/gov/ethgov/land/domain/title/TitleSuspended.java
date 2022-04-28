package et.gov.ethgov.land.domain.title;

import java.time.Instant;
import java.util.Objects;

/** Domain event: TitleSuspended. */
public final class TitleSuspended {
    private final TitleId id;
    private final Instant occurredAt;

    public TitleSuspended(TitleId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public TitleId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
