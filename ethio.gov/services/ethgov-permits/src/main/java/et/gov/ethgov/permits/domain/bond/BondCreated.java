package et.gov.ethgov.permits.domain.bond;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BondCreated. */
public final class BondCreated {
    private final BondId id;
    private final Instant occurredAt;

    public BondCreated(BondId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BondId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
