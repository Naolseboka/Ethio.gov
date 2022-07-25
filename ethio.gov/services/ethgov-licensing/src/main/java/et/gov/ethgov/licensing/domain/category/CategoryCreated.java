package et.gov.ethgov.licensing.domain.category;

import java.time.Instant;
import java.util.Objects;

/** Domain event: CategoryCreated. */
public final class CategoryCreated {
    private final CategoryId id;
    private final Instant occurredAt;

    public CategoryCreated(CategoryId id, Instant occurredAt) {
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
