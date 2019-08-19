package et.gov.ethgov.citizen.domain.documentlink;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocumentlinkUpdated. */
public final class DocumentlinkUpdated {
    private final DocumentlinkId id;
    private final Instant occurredAt;

    public DocumentlinkUpdated(DocumentlinkId id, Instant occurredAt) {
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
