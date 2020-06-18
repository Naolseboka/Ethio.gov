package et.gov.ethgov.directory.domain.unit;

import java.time.Instant;

public record OrganizationalUnitDeactivated(
        OrganizationalUnitId organizationalUnitId, Instant occurredAt)
        implements OrganizationalUnitDomainEvent {}
