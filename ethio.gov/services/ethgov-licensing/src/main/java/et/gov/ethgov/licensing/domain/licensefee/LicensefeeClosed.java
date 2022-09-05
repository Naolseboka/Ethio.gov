package et.gov.ethgov.licensing.domain.licensefee;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LicensefeeClosed. */
public final class LicensefeeClosed {
    private final LicensefeeId id;
    private final Instant occurredAt;

    public LicensefeeClosed(LicensefeeId id, Instant occurredAt) {
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
