package et.gov.ethgov.organization.domain.orgunit;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OrgunitUpdated. */
public final class OrgunitUpdated {
    private final OrgunitId id;
    private final Instant occurredAt;

    public OrgunitUpdated(OrgunitId id, Instant occurredAt) {
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
