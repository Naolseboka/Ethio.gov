package et.gov.ethgov.citizen.domain.documentlink;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocumentlinkCreated. */
public final class DocumentlinkCreated {
    private final DocumentlinkId id;
    private final Instant occurredAt;

    public DocumentlinkCreated(DocumentlinkId id, Instant occurredAt) {
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
