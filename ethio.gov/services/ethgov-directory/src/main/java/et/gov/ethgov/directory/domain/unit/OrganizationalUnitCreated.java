package et.gov.ethgov.directory.domain.unit;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record OrganizationalUnitCreated(
        OrganizationalUnitId organizationalUnitId,
        TenantId tenantId,
        UnitCode code,
        OrganizationalUnitType type,
        Instant occurredAt)
        implements OrganizationalUnitDomainEvent {}
