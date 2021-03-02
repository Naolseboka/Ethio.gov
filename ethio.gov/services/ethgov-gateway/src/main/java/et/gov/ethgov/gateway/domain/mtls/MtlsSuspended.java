package et.gov.ethgov.gateway.domain.mtls;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MtlsSuspended. */
public final class MtlsSuspended {
    private final MtlsId id;
    private final Instant occurredAt;

    public MtlsSuspended(MtlsId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MtlsId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
