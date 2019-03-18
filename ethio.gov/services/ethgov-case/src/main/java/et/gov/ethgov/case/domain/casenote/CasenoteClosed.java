package et.gov.ethgov.case.domain.casenote;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CasenoteClosed. */
public final class CasenoteClosed {
    private final CasenoteId id;
    private final Instant occurredAt;

    public CasenoteClosed(CasenoteId id, Instant occurredAt) {
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
