package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.application.storage.ObjectStoragePort;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.document.domain.document.DocumentStatus;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateDocumentDownloadUrlUseCase {

    private final DocumentRepository documents;
    private final ObjectStoragePort objectStorage;

    public CreateDocumentDownloadUrlUseCase(DocumentRepository documents, ObjectStoragePort objectStorage) {
        this.documents = documents;
        this.objectStorage = objectStorage;
    }

    @Transactional(readOnly = true)
    public URI execute(String documentId) {
        Objects.requireNonNull(documentId, "documentId");
        Document document = documents
                .findById(DocumentId.of(documentId))
                .orElseThrow(() -> new DomainException(
                        "DOCUMENT_NOT_FOUND",
                        "Document Not Found",
                        "Document does not exist",
                        404));
        if (document.status() != DocumentStatus.ACTIVE && document.status() != DocumentStatus.ARCHIVED) {
            throw new DomainException(
                    "DOCUMENT_NOT_DOWNLOADABLE",
                    "Document Not Downloadable",
                    "Only active or archived documents can be downloaded",
                    409);
        }
        return objectStorage.createDownloadUrl(document.storageObjectKey(), Duration.ofMinutes(15));
    }
}
