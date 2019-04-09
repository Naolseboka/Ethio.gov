package et.gov.ethgov.case.domain.party;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PartyClosed. */
public final class PartyClosed {
    private final PartyId id;
    private final Instant occurredAt;

    public PartyClosed(PartyId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PartyId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
