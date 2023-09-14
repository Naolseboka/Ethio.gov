package et.gov.ethgov.organization.domain.orgaddress;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OrgaddressActivated. */
public final class OrgaddressActivated {
    private final OrgaddressId id;
    private final Instant occurredAt;

    public OrgaddressActivated(OrgaddressId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OrgaddressId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
