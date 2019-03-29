package et.gov.ethgov.case.domain.evidence;

import java.time.Instant;
import java.util.Objects;

/** Domain event: EvidenceActivated. */
public final class EvidenceActivated {
    private final EvidenceId id;
    private final Instant occurredAt;

    public EvidenceActivated(EvidenceId id, Instant occurredAt) {
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
