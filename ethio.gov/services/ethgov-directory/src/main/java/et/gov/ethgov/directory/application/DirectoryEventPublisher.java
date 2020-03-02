package et.gov.ethgov.directory.application;

import et.gov.ethgov.directory.domain.employee.PublicEmployeeDomainEvent;
import et.gov.ethgov.directory.domain.unit.OrganizationalUnitDomainEvent;

public interface DirectoryEventPublisher {

    void publishUnitEvents(Iterable<OrganizationalUnitDomainEvent> events);

    void publishEmployeeEvents(Iterable<PublicEmployeeDomainEvent> events);
}
