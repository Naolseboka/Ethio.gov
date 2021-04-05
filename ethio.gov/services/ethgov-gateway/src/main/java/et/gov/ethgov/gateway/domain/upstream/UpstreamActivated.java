package et.gov.ethgov.gateway.domain.upstream;

import java.time.Instant;
import java.util.Objects;

/** Domain event: UpstreamActivated. */
public final class UpstreamActivated {
    private final UpstreamId id;
    private final Instant occurredAt;

    public UpstreamActivated(UpstreamId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public UpstreamId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
