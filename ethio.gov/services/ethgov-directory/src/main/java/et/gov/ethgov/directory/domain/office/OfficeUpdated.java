package et.gov.ethgov.directory.domain.office;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OfficeUpdated. */
public final class OfficeUpdated {
    private final OfficeId id;
    private final Instant occurredAt;

    public OfficeUpdated(OfficeId id, Instant occurredAt) {
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
