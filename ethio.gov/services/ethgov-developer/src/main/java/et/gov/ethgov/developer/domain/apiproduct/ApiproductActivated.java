package et.gov.ethgov.developer.domain.apiproduct;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ApiproductActivated. */
public final class ApiproductActivated {
    private final ApiproductId id;
    private final Instant occurredAt;

    public ApiproductActivated(ApiproductId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ApiproductId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
