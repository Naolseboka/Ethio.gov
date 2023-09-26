package et.gov.ethgov.organization.domain.orgcontact;

import java.time.Instant;
import java.util.Objects;

/** Domain event: OrgcontactActivated. */
public final class OrgcontactActivated {
    private final OrgcontactId id;
    private final Instant occurredAt;

    public OrgcontactActivated(OrgcontactId id, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public OrgcontactId id() {
        return id;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
