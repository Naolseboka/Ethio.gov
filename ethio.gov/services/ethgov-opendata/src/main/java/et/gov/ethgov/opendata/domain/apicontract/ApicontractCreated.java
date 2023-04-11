package et.gov.ethgov.opendata.domain.apicontract;

import java.time.Instant;
import java.util.Objects;

/** Domain event: ApicontractCreated. */
public final class ApicontractCreated {
    private final ApicontractId id;
    private final Instant occurredAt;

    public ApicontractCreated(ApicontractId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public ApicontractId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
