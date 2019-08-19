package et.gov.ethgov.citizen.domain.documentlink;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocumentlinkSuspended. */
public final class DocumentlinkSuspended {
    private final DocumentlinkId id;
    private final Instant occurredAt;

    public DocumentlinkSuspended(DocumentlinkId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DocumentlinkId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
