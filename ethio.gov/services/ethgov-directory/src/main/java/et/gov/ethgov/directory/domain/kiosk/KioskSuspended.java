package et.gov.ethgov.directory.domain.kiosk;

import java.time.Instant;
import java.util.Objects;

/** Domain event: KioskSuspended. */
public final class KioskSuspended {
    private final KioskId id;
    private final Instant occurredAt;

    public KioskSuspended(KioskId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public KioskId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
