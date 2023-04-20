package et.gov.ethgov.opendata.domain.datalicense;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DatalicenseActivated. */
public final class DatalicenseActivated {
    private final DatalicenseId id;
    private final Instant occurredAt;

    public DatalicenseActivated(DatalicenseId id, Instant occurredAt) {
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
