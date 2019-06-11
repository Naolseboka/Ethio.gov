package et.gov.ethgov.citizen.application.citizen;

import et.gov.ethgov.citizen.domain.citizen.CitizenDomainEvent;

public interface CitizenEventPublisher {

    void publishAll(Iterable<CitizenDomainEvent> events);
}
