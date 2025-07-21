package et.gov.ethgov.search.domain.document;

import java.time.Instant;

public record SearchDocumentRetired(SearchDocumentId searchDocumentId, String reason, Instant occurredAt)
        implements SearchDocumentDomainEvent {}
