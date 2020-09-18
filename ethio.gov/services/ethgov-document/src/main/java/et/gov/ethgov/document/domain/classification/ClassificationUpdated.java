package et.gov.ethgov.document.domain.classification;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ClassificationUpdated. */
public final class ClassificationUpdated {
    private final ClassificationId id;
    private final Instant occurredAt;

    public ClassificationUpdated(ClassificationId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ClassificationId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
