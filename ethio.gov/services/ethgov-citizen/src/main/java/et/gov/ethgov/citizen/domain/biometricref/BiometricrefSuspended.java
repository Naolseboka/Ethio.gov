package et.gov.ethgov.citizen.domain.biometricref;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BiometricrefSuspended. */
public final class BiometricrefSuspended {
    private final BiometricrefId id;
    private final Instant occurredAt;

    public BiometricrefSuspended(BiometricrefId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public BiometricrefId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
