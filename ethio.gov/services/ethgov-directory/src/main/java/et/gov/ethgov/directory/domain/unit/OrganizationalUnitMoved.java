package et.gov.ethgov.directory.domain.unit;

import java.time.Instant;

public record OrganizationalUnitMoved(
        OrganizationalUnitId organizationalUnitId,
        OrganizationalUnitId newParentId,
        Instant occurredAt)
        implements OrganizationalUnitDomainEvent {}
