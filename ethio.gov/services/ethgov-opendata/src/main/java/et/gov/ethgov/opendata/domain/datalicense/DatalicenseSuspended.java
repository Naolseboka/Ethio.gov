package et.gov.ethgov.opendata.domain.datalicense;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DatalicenseSuspended. */
public final class DatalicenseSuspended {
    private final DatalicenseId id;
    private final Instant occurredAt;

    public DatalicenseSuspended(DatalicenseId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DatalicenseId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
