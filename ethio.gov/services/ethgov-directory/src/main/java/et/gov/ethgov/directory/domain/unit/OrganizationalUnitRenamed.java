package et.gov.ethgov.directory.domain.unit;

import java.time.Instant;

public record OrganizationalUnitRenamed(
        OrganizationalUnitId organizationalUnitId, UnitName name, Instant occurredAt)
        implements OrganizationalUnitDomainEvent {}
