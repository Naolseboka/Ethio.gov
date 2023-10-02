package et.gov.ethgov.organization.domain.orgunit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OrgunitActivated. */
public final class OrgunitActivated {
    private final OrgunitId id;
    private final Instant occurredAt;

    public OrgunitActivated(OrgunitId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OrgunitId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
