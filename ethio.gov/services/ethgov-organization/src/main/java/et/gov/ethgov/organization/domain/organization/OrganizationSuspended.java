package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public record OrganizationSuspended(OrganizationId organizationId, String reason, Instant occurredAt)
        implements OrganizationDomainEvent {}
