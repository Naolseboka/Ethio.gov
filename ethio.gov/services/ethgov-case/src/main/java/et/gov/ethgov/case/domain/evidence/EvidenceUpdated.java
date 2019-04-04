package et.gov.ethgov.case.domain.evidence;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EvidenceUpdated. */
public final class EvidenceUpdated {
    private final EvidenceId id;
    private final Instant occurredAt;

    public EvidenceUpdated(EvidenceId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public EvidenceId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
