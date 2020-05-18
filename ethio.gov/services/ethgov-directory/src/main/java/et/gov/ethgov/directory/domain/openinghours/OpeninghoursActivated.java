package et.gov.ethgov.directory.domain.openinghours;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OpeninghoursActivated. */
public final class OpeninghoursActivated {
    private final OpeninghoursId id;
    private final Instant occurredAt;

    public OpeninghoursActivated(OpeninghoursId id, Instant occurredAt) {
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
