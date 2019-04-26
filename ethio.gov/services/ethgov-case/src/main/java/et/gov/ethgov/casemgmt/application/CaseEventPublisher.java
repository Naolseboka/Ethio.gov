package et.gov.ethgov.casemgmt.application;

import et.gov.ethgov.casemgmt.domain.casefile.CaseDomainEvent;

public interface CaseEventPublisher {
    void publishAll(Iterable<CaseDomainEvent> events);
}
