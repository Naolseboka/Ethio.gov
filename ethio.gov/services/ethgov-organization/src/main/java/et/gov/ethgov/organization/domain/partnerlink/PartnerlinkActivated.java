package et.gov.ethgov.organization.domain.partnerlink;

import java.time.Instant;
import java.util.Objects;

/** Domain event: PartnerlinkActivated. */
public final class PartnerlinkActivated {
    private final PartnerlinkId id;
    private final Instant occurredAt;

    public PartnerlinkActivated(PartnerlinkId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public PartnerlinkId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
