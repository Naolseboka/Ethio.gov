package et.gov.ethgov.citizen.domain.addresshistory;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AddresshistorySuspended. */
public final class AddresshistorySuspended {
    private final AddresshistoryId id;
    private final Instant occurredAt;

    public AddresshistorySuspended(AddresshistoryId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AddresshistoryId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
