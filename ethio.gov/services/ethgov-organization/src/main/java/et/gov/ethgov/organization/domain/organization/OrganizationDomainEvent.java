package et.gov.ethgov.organization.domain.organization;

import java.time.Instant;

public sealed interface OrganizationDomainEvent
        permits OrganizationRegistered,
                OrganizationProfileUpdated,
                OrganizationTinAssigned,
                OrganizationSuspended,
                OrganizationReinstated,
                OrganizationDissolved {

    OrganizationId organizationId();

    Instant occurredAt();
}
