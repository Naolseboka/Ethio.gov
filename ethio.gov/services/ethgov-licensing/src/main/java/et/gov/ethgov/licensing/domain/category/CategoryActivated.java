package et.gov.ethgov.licensing.domain.category;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CategoryActivated. */
public final class CategoryActivated {
    private final CategoryId id;
    private final Instant occurredAt;

    public CategoryActivated(CategoryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public CategoryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
