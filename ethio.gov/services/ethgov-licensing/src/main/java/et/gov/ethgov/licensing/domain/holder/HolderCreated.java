package et.gov.ethgov.licensing.domain.holder;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HolderCreated. */
public final class HolderCreated {
    private final HolderId id;
    private final Instant occurredAt;

    public HolderCreated(HolderId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public HolderId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
