package et.gov.ethgov.case.domain.casenote;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CasenoteActivated. */
public final class CasenoteActivated {
    private final CasenoteId id;
    private final Instant occurredAt;

    public CasenoteActivated(CasenoteId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CasenoteId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
