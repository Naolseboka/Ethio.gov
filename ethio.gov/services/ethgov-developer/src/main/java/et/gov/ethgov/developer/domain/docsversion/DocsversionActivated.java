package et.gov.ethgov.developer.domain.docsversion;

import java.time.Instant;
import java.util.Objects;

/** Domain event: DocsversionActivated. */
public final class DocsversionActivated {
    private final DocsversionId id;
    private final Instant occurredAt;

    public DocsversionActivated(DocsversionId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public DocsversionId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
