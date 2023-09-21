package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public record OrganizationProfileUpdated(OrganizationId organizationId, Instant occurredAt)
        implements OrganizationDomainEvent {}
