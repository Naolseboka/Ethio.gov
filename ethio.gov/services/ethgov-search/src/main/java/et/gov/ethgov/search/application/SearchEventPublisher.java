package et.gov.ethgov.search.application;

import et.gov.ethgov.search.domain.document.SearchDocumentDomainEvent;

public interface SearchEventPublisher {
    void publishAll(Iterable<SearchDocumentDomainEvent> events);
}
