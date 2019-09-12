package et.gov.ethgov.citizen.domain.namechange;

import java.time.Instant;
import java.util.Objects;

/** Domain event: NamechangeSuspended. */
public final class NamechangeSuspended {
    private final NamechangeId id;
    private final Instant occurredAt;

    public NamechangeSuspended(NamechangeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public NamechangeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
