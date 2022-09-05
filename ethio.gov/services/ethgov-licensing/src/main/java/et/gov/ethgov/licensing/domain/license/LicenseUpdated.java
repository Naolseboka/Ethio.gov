package et.gov.ethgov.licensing.domain.license;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LicenseUpdated. */
public final class LicenseUpdated {
    private final LicenseId id;
    private final Instant occurredAt;

    public LicenseUpdated(LicenseId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public LicenseId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
