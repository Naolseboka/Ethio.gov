package et.gov.ethgov.workflow.domain.definition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DefinitionSuspended. */
public final class DefinitionSuspended {
    private final DefinitionId id;
    private final Instant occurredAt;

    public DefinitionSuspended(DefinitionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DefinitionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
