package et.gov.ethgov.licensing.domain.holder;

import java.time.Instant;
import java.util.Objects;

/** Domain event: HolderActivated. */
public final class HolderActivated {
    private final HolderId id;
    private final Instant occurredAt;

    public HolderActivated(HolderId id, Instant occurredAt) {
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
