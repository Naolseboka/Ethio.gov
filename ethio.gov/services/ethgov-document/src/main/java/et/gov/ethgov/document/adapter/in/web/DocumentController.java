package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.document.ArchiveDocumentUseCase;
import et.gov.ethgov.document.application.document.CompleteDocumentUploadUseCase;
import et.gov.ethgov.document.application.document.CreateDocumentDownloadUrlUseCase;
import et.gov.ethgov.document.application.document.DeleteDocumentUseCase;
import et.gov.ethgov.document.application.document.GetDocumentUseCase;
import et.gov.ethgov.document.application.document.InitiateDocumentUploadUseCase;
import et.gov.ethgov.document.application.document.UpdateDocumentMetadataUseCase;
import et.gov.ethgov.document.domain.document.Document;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/documents")
public class DocumentController {

    private final InitiateDocumentUploadUseCase initiateUpload;
    private final CompleteDocumentUploadUseCase completeUpload;
    private final CreateDocumentDownloadUrlUseCase createDownloadUrl;
    private final GetDocumentUseCase getDocument;
    private final UpdateDocumentMetadataUseCase updateMetadata;
    private final ArchiveDocumentUseCase archiveDocument;
    private final DeleteDocumentUseCase deleteDocument;

    public DocumentController(
            InitiateDocumentUploadUseCase initiateUpload,
            CompleteDocumentUploadUseCase completeUpload,
            CreateDocumentDownloadUrlUseCase createDownloadUrl,
            GetDocumentUseCase getDocument,
            UpdateDocumentMetadataUseCase updateMetadata,
            ArchiveDocumentUseCase archiveDocument,
            DeleteDocumentUseCase deleteDocument) {
        this.initiateUpload = initiateUpload;
        this.completeUpload = completeUpload;
        this.createDownloadUrl = createDownloadUrl;
        this.getDocument = getDocument;
        this.updateMetadata = updateMetadata;
        this.archiveDocument = archiveDocument;
        this.deleteDocument = deleteDocument;
    }

    @PostMapping("/uploads")
    @ResponseStatus(HttpStatus.CREATED)
    public InitiateUploadResponse initiate(@Valid @RequestBody InitiateUploadRequest request) {
        InitiateDocumentUploadUseCase.Result result = initiateUpload.execute(
                new InitiateDocumentUploadUseCase.Command(
                        request.tenantId(),
                        request.title(),
                        request.description(),
                        request.classification(),
                        request.contentType(),
                        request.fileName(),
                        request.expectedSizeBytes(),
                        request.ownerType(),
                        request.ownerId(),
                        request.uploadedByAccountId()));
        return new InitiateUploadResponse(
                result.documentId().value(), result.uploadUrl().toString(), result.storageObjectKey());
    }

    @PostMapping("/{documentId}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(
            @PathVariable String documentId, @Valid @RequestBody CompleteUploadRequest request) {
        completeUpload.execute(documentId, request.contentHash());
    }

    @GetMapping("/{documentId}")
    public DocumentResponse get(@PathVariable String documentId) {
        return DocumentResponse.from(getDocument.byId(documentId));
    }

    @PostMapping("/{documentId}/download-url")
    public DownloadUrlResponse downloadUrl(@PathVariable String documentId) {
        URI url = createDownloadUrl.execute(documentId);
        return new DownloadUrlResponse(url.toString());
    }

    @PutMapping("/{documentId}/metadata")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMetadata(
            @PathVariable String documentId, @Valid @RequestBody UpdateMetadataRequest request) {
        updateMetadata.execute(new UpdateDocumentMetadataUseCase.Command(
                documentId, request.title(), request.description(), request.classification()));
    }

    @PostMapping("/{documentId}/archive")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable String documentId) {
        archiveDocument.execute(documentId);
    }

    @PostMapping("/{documentId}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String documentId) {
        deleteDocument.execute(documentId);
    }

    public record InitiateUploadRequest(
            @NotBlank String tenantId,
            @NotBlank String title,
            String description,
            @NotBlank String classification,
            @NotBlank String contentType,
            @NotBlank String fileName,
            @NotNull @Min(1) @Max(52_428_800) Long expectedSizeBytes,
            String ownerType,
            String ownerId,
            String uploadedByAccountId) {}

    public record CompleteUploadRequest(@NotBlank @Size(min = 64, max = 64) String contentHash) {}

    public record UpdateMetadataRequest(
            @NotBlank String title, String description, @NotBlank String classification) {}

    public record InitiateUploadResponse(String documentId, String uploadUrl, String storageObjectKey) {}

    public record DownloadUrlResponse(String downloadUrl) {}

    public record DocumentResponse(
            String documentId,
            String tenantId,
            String title,
            String description,
            String classification,
            String status,
            String contentType,
            String originalFileName,
            String storageObjectKey,
            int version,
            Long sizeBytes,
            String contentHash,
            String ownerType,
            String ownerId,
            String uploadedByAccountId) {

        static DocumentResponse from(Document document) {
            return new DocumentResponse(
                    document.id().value(),
                    document.tenantId().value(),
                    document.title(),
                    document.description().orElse(null),
                    document.classification().name(),
                    document.status().name(),
                    document.contentType().value(),
                    document.originalFileName().value(),
                    document.storageObjectKey().value(),
                    document.version(),
                    document.sizeBytes().orElse(null),
                    document.contentHash().map(Object::toString).orElse(null),
                    document.owner().map(o -> o.type()).orElse(null),
                    document.owner().map(o -> o.id()).orElse(null),
                    document.uploadedByAccountId().orElse(null));
        }
    }
}
