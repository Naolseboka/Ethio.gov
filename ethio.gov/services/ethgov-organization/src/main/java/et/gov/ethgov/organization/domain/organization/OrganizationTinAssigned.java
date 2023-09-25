package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public record OrganizationTinAssigned(OrganizationId organizationId, TinNumber tin, Instant occurredAt)
        implements OrganizationDomainEvent {}
