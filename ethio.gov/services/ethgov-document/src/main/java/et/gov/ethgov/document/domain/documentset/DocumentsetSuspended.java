package et.gov.ethgov.document.domain.documentset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocumentsetSuspended. */
public final class DocumentsetSuspended {
    private final DocumentsetId id;
    private final Instant occurredAt;

    public DocumentsetSuspended(DocumentsetId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DocumentsetId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
