package et.gov.ethgov.licensing.domain.license;

import java.time.Instant;
import java.util.Objects;

/** Domain event: LicenseActivated. */
public final class LicenseActivated {
    private final LicenseId id;
    private final Instant occurredAt;

    public LicenseActivated(LicenseId id, Instant occurredAt) {
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
