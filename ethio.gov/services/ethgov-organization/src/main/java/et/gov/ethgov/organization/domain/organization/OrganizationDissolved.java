package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public record OrganizationDissolved(OrganizationId organizationId, Instant occurredAt)
        implements OrganizationDomainEvent {}
