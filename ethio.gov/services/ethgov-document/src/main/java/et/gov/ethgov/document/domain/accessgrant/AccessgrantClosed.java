package et.gov.ethgov.document.domain.accessgrant;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AccessgrantClosed. */
public final class AccessgrantClosed {
    private final AccessgrantId id;
    private final Instant occurredAt;

    public AccessgrantClosed(AccessgrantId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AccessgrantId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
