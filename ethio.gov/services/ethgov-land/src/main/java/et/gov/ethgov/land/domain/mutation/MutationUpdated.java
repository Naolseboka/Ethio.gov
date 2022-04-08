package et.gov.ethgov.land.domain.mutation;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MutationUpdated. */
public final class MutationUpdated {
    private final MutationId id;
    private final Instant occurredAt;

    public MutationUpdated(MutationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MutationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
