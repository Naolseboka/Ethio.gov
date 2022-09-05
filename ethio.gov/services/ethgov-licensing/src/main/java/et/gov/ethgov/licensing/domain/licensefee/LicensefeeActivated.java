package et.gov.ethgov.licensing.domain.licensefee;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LicensefeeActivated. */
public final class LicensefeeActivated {
    private final LicensefeeId id;
    private final Instant occurredAt;

    public LicensefeeActivated(LicensefeeId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LicensefeeId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
