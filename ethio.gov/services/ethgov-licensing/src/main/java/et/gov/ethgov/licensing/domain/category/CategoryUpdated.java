package et.gov.ethgov.licensing.domain.category;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CategoryUpdated. */
public final class CategoryUpdated {
    private final CategoryId id;
    private final Instant occurredAt;

    public CategoryUpdated(CategoryId id, Instant occurredAt) {
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
