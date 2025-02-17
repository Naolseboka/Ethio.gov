package et.gov.ethgov.revenue.domain.arrears;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ArrearsClosed. */
public final class ArrearsClosed {
    private final ArrearsId id;
    private final Instant occurredAt;

    public ArrearsClosed(ArrearsId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ArrearsId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
