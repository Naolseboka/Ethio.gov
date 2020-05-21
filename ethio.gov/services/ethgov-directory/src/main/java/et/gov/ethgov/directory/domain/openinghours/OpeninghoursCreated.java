package et.gov.ethgov.directory.domain.openinghours;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OpeninghoursCreated. */
public final class OpeninghoursCreated {
    private final OpeninghoursId id;
    private final Instant occurredAt;

    public OpeninghoursCreated(OpeninghoursId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OpeninghoursId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
