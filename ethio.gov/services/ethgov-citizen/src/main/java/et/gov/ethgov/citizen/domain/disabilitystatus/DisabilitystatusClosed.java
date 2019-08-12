package et.gov.ethgov.citizen.domain.disabilitystatus;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DisabilitystatusClosed. */
public final class DisabilitystatusClosed {
    private final DisabilitystatusId id;
    private final Instant occurredAt;

    public DisabilitystatusClosed(DisabilitystatusId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DisabilitystatusId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
