package et.gov.ethgov.search.domain.document;

import java.time.Instant;

public sealed interface SearchDocumentDomainEvent
        permits SearchDocumentIndexed, SearchDocumentUpdated, SearchDocumentRetired {
    SearchDocumentId searchDocumentId();

    Instant occurredAt();
}
