package et.gov.ethgov.organization.domain.organization;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record OrganizationRegistered(
        OrganizationId organizationId,
        TenantId registeringTenantId,
        OrganizationType type,
        Instant occurredAt)
        implements OrganizationDomainEvent {}
