package et.gov.ethgov.document.domain.document;

import java.time.Instant;

public sealed interface DocumentDomainEvent
        permits DocumentDraftCreated, DocumentUploaded, DocumentArchived, DocumentDeleted, DocumentMetadataUpdated {

    DocumentId documentId();

    Instant occurredAt();
}
