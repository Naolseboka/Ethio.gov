package et.gov.ethgov.document.domain.renderjob;

import java.time.Instant;
import java.util.Objects;

/** Domain event: RenderjobSuspended. */
public final class RenderjobSuspended {
    private final RenderjobId id;
    private final Instant occurredAt;

    public RenderjobSuspended(RenderjobId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public RenderjobId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
