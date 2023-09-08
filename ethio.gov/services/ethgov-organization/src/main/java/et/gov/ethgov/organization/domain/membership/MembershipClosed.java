package et.gov.ethgov.organization.domain.membership;

import java.time.Instant;
import java.util.Objects;

/** Domain event: MembershipClosed. */
public final class MembershipClosed {
    private final MembershipId id;
    private final Instant occurredAt;

    public MembershipClosed(MembershipId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public MembershipId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
