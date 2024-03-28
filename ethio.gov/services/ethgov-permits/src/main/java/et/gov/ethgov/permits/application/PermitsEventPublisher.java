package et.gov.ethgov.permits.application;

import et.gov.ethgov.permits.domain.permit.PermitDomainEvent;

public interface PermitsEventPublisher {
    void publishAll(Iterable<PermitDomainEvent> events);
}
