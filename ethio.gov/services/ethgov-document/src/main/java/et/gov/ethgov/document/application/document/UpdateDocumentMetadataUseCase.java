package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.application.DocumentEventPublisher;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentClassification;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateDocumentMetadataUseCase {

    private final DocumentRepository documents;
    private final DocumentEventPublisher eventPublisher;
    private final Clock clock;

    public UpdateDocumentMetadataUseCase(
            DocumentRepository documents, DocumentEventPublisher eventPublisher, Clock clock) {
        this.documents = documents;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(Command command) {
        Objects.requireNonNull(command, "command");
        Document document = documents
                .findById(DocumentId.of(command.documentId()))
                .orElseThrow(() -> new DomainException(
                        "DOCUMENT_NOT_FOUND",
                        "Document Not Found",
                        "Document does not exist",
                        404));
        document.updateMetadata(
                command.title(),
                command.description(),
                DocumentClassification.valueOf(command.classification()),
                clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
    }

    public record Command(String documentId, String title, String description, String classification) {}
}
