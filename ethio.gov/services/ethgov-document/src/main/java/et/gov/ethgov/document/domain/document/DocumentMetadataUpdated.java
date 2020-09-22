package et.gov.ethgov.document.domain.document;

import java.time.Instant;

public record DocumentMetadataUpdated(DocumentId documentId, Instant occurredAt)
        implements DocumentDomainEvent {}
