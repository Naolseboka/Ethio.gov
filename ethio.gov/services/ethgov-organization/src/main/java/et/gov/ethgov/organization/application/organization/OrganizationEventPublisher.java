package et.gov.ethgov.organization.application.organization;

import et.gov.ethgov.organization.domain.organization.OrganizationDomainEvent;

public interface OrganizationEventPublisher {

    void publishAll(Iterable<OrganizationDomainEvent> events);
}
