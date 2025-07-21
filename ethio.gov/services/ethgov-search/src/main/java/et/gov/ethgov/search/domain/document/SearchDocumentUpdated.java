package et.gov.ethgov.search.domain.document;

import java.time.Instant;

public record SearchDocumentUpdated(SearchDocumentId searchDocumentId, Instant occurredAt)
        implements SearchDocumentDomainEvent {}
