package et.gov.ethgov.directory.domain.unit;

import java.time.Instant;

public record OrganizationalUnitReactivated(
        OrganizationalUnitId organizationalUnitId, Instant occurredAt)
        implements OrganizationalUnitDomainEvent {}
