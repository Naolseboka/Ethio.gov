package et.gov.ethgov.case.domain.casefile;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CasefileCreated. */
public final class CasefileCreated {
    private final CasefileId id;
    private final Instant occurredAt;

    public CasefileCreated(CasefileId id, Instant occurredAt) {
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
