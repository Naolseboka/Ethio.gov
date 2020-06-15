package et.gov.ethgov.directory.domain.staffroster;

import java.time.Instant;
import java.util.Objects;

/** Domain event: StaffrosterClosed. */
public final class StaffrosterClosed {
    private final StaffrosterId id;
    private final Instant occurredAt;

    public StaffrosterClosed(StaffrosterId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public StaffrosterId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
