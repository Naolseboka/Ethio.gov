package et.gov.ethgov.case.domain.party;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PartyCreated. */
public final class PartyCreated {
    private final PartyId id;
    private final Instant occurredAt;

    public PartyCreated(PartyId id, Instant occurredAt) {
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
