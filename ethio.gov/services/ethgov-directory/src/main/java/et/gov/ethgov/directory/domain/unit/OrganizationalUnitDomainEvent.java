package et.gov.ethgov.directory.domain.unit;

import java.time.Instant;

public sealed interface OrganizationalUnitDomainEvent
        permits OrganizationalUnitCreated,
                OrganizationalUnitRenamed,
                OrganizationalUnitMoved,
                OrganizationalUnitDeactivated,
                OrganizationalUnitReactivated {

    OrganizationalUnitId organizationalUnitId();

    Instant occurredAt();
}
