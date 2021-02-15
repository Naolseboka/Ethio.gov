package et.gov.ethgov.gateway.domain.cors;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CorsClosed. */
public final class CorsClosed {
    private final CorsId id;
    private final Instant occurredAt;

    public CorsClosed(CorsId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CorsId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
