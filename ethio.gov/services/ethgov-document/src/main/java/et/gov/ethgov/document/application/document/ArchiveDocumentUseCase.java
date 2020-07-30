package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.application.DocumentEventPublisher;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArchiveDocumentUseCase {

    private final DocumentRepository documents;
    private final DocumentEventPublisher eventPublisher;
    private final Clock clock;

    public ArchiveDocumentUseCase(
            DocumentRepository documents, DocumentEventPublisher eventPublisher, Clock clock) {
        this.documents = documents;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String documentId) {
        Objects.requireNonNull(documentId, "documentId");
        Document document = documents
                .findById(DocumentId.of(documentId))
                .orElseThrow(() -> new DomainException(
                        "DOCUMENT_NOT_FOUND",
                        "Document Not Found",
                        "Document does not exist",
                        404));
        document.archive(clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
    }
}
