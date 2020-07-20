package et.gov.ethgov.document.application;

import et.gov.ethgov.document.domain.document.DocumentDomainEvent;

public interface DocumentEventPublisher {

    void publishAll(Iterable<DocumentDomainEvent> events);
}
