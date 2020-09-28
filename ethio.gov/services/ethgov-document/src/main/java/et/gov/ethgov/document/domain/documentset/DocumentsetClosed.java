package et.gov.ethgov.document.domain.documentset;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocumentsetClosed. */
public final class DocumentsetClosed {
    private final DocumentsetId id;
    private final Instant occurredAt;

    public DocumentsetClosed(DocumentsetId id, Instant occurredAt) {
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
