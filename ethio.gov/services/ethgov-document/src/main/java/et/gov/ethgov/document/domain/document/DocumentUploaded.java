package et.gov.ethgov.document.domain.document;

import java.time.Instant;

public record DocumentUploaded(
        DocumentId documentId, ContentHash contentHash, long sizeBytes, Instant occurredAt)
        implements DocumentDomainEvent {}
