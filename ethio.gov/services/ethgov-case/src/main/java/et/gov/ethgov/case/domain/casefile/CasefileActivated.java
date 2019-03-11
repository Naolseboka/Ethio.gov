package et.gov.ethgov.case.domain.casefile;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CasefileActivated. */
public final class CasefileActivated {
    private final CasefileId id;
    private final Instant occurredAt;

    public CasefileActivated(CasefileId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CasefileId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
