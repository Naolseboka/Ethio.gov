package et.gov.ethgov.gateway.domain.cors;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CorsCreated. */
public final class CorsCreated {
    private final CorsId id;
    private final Instant occurredAt;

    public CorsCreated(CorsId id, Instant occurredAt) {
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
