package et.gov.ethgov.citizen.domain.addresshistory;

import java.time.Instant;
import java.util.Objects;

/** Domain event: AddresshistoryClosed. */
public final class AddresshistoryClosed {
    private final AddresshistoryId id;
    private final Instant occurredAt;

    public AddresshistoryClosed(AddresshistoryId id, Instant occurredAt) {
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
