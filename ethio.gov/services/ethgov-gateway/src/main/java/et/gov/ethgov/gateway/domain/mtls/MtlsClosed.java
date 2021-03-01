package et.gov.ethgov.gateway.domain.mtls;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MtlsClosed. */
public final class MtlsClosed {
    private final MtlsId id;
    private final Instant occurredAt;

    public MtlsClosed(MtlsId id, Instant occurredAt) {
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
