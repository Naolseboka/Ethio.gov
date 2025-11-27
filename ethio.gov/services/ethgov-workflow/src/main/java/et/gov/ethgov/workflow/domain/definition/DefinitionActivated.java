package et.gov.ethgov.workflow.domain.definition;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DefinitionActivated. */
public final class DefinitionActivated {
    private final DefinitionId id;
    private final Instant occurredAt;

    public DefinitionActivated(DefinitionId id, Instant occurredAt) {
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
