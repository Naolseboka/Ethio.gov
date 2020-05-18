package et.gov.ethgov.directory.domain.office;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OfficeSuspended. */
public final class OfficeSuspended {
    private final OfficeId id;
    private final Instant occurredAt;

    public OfficeSuspended(OfficeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OfficeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
