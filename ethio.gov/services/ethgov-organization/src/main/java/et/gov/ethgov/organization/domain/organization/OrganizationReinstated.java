package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public record OrganizationReinstated(OrganizationId organizationId, Instant occurredAt)
        implements OrganizationDomainEvent {}
