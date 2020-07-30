package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.application.DocumentEventPublisher;
import et.gov.ethgov.document.application.storage.ObjectStoragePort;
import et.gov.ethgov.document.domain.document.ContentHash;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompleteDocumentUploadUseCase {

    private final DocumentRepository documents;
    private final ObjectStoragePort objectStorage;
    private final DocumentEventPublisher eventPublisher;
    private final Clock clock;

    public CompleteDocumentUploadUseCase(
            DocumentRepository documents,
            ObjectStoragePort objectStorage,
            DocumentEventPublisher eventPublisher,
            Clock clock) {
        this.documents = documents;
        this.objectStorage = objectStorage;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String documentId, String contentHashHex) {
        Objects.requireNonNull(documentId, "documentId");
        Document document = documents
                .findById(DocumentId.of(documentId))
                .orElseThrow(() -> new DomainException(
                        "DOCUMENT_NOT_FOUND",
                        "Document Not Found",
                        "Document does not exist",
                        404));
        ObjectStoragePort.StoredObjectMetadata metadata = objectStorage
                .head(document.storageObjectKey())
                .orElseThrow(() -> new DomainException(
                        "OBJECT_NOT_FOUND",
                        "Object Not Found",
                        "Uploaded object was not found in storage; upload may have failed",
                        409));
        if (metadata.contentType() != null
                && !metadata.contentType().equalsIgnoreCase(document.contentType().value())
                && !metadata.contentType().startsWith(document.contentType().value())) {
            throw new DomainException(
                    "CONTENT_TYPE_MISMATCH",
                    "Content Type Mismatch",
                    "Stored object content type does not match the document declaration",
                    409);
        }
        document.completeUpload(metadata.contentLength(), ContentHash.of(contentHashHex), clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
    }
}
