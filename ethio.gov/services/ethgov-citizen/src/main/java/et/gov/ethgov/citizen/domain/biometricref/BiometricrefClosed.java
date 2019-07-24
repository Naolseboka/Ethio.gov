package et.gov.ethgov.citizen.domain.biometricref;

import java.time.Instant;
import java.util.Objects;

/** Domain event: BiometricrefClosed. */
public final class BiometricrefClosed {
    private final BiometricrefId id;
    private final Instant occurredAt;

    public BiometricrefClosed(BiometricrefId id, Instant occurredAt) {
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
