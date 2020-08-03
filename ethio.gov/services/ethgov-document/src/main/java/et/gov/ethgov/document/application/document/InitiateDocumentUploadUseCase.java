package et.gov.ethgov.document.application.document;

import et.gov.ethgov.document.application.DocumentEventPublisher;
import et.gov.ethgov.document.application.storage.ObjectStoragePort;
import et.gov.ethgov.document.domain.document.ContentType;
import et.gov.ethgov.document.domain.document.Document;
import et.gov.ethgov.document.domain.document.DocumentClassification;
import et.gov.ethgov.document.domain.document.DocumentId;
import et.gov.ethgov.document.domain.document.DocumentOwnerRef;
import et.gov.ethgov.document.domain.document.DocumentRepository;
import et.gov.ethgov.document.domain.document.FileName;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InitiateDocumentUploadUseCase {

    private final DocumentRepository documents;
    private final ObjectStoragePort objectStorage;
    private final DocumentEventPublisher eventPublisher;
    private final Clock clock;

    public InitiateDocumentUploadUseCase(
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
    public Result execute(Command command) {
        Objects.requireNonNull(command, "command");
        DocumentOwnerRef owner = null;
        if (command.ownerType() != null && !command.ownerType().isBlank()) {
            owner = DocumentOwnerRef.of(command.ownerType(), command.ownerId());
        }
        Document document = Document.createDraft(
                TenantId.of(command.tenantId()),
                command.title(),
                command.description(),
                DocumentClassification.valueOf(command.classification()),
                ContentType.of(command.contentType()),
                FileName.of(command.fileName()),
                owner,
                command.uploadedByAccountId(),
                command.expectedSizeBytes(),
                clock.instant());
        documents.save(document);
        eventPublisher.publishAll(document.pullDomainEvents());
        URI uploadUrl = objectStorage.createUploadUrl(
                document.storageObjectKey(),
                document.contentType(),
                command.expectedSizeBytes(),
                Duration.ofMinutes(15));
        return new Result(document.id(), uploadUrl, document.storageObjectKey().value());
    }

    public record Command(
            String tenantId,
            String title,
            String description,
            String classification,
            String contentType,
            String fileName,
            long expectedSizeBytes,
            String ownerType,
            String ownerId,
            String uploadedByAccountId) {}

    public record Result(DocumentId documentId, URI uploadUrl, String storageObjectKey) {}
}
