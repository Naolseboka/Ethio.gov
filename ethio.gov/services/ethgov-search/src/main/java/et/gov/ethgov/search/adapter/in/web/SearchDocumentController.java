package et.gov.ethgov.search.adapter.in.web;

import et.gov.ethgov.search.application.document.GetSearchDocumentUseCase;
import et.gov.ethgov.search.application.document.IndexSearchDocumentUseCase;
import et.gov.ethgov.search.application.document.QuerySearchDocumentsUseCase;
import et.gov.ethgov.search.application.document.RetireSearchDocumentUseCase;
import et.gov.ethgov.search.application.document.UpdateSearchDocumentUseCase;
import et.gov.ethgov.search.domain.document.SearchDocument;
import et.gov.ethgov.search.domain.document.SearchHit;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/search")
public class SearchDocumentController {

    private final IndexSearchDocumentUseCase indexDocument;
    private final UpdateSearchDocumentUseCase updateDocument;
    private final RetireSearchDocumentUseCase retireDocument;
    private final GetSearchDocumentUseCase getDocument;
    private final QuerySearchDocumentsUseCase queryDocuments;

    public SearchDocumentController(
            IndexSearchDocumentUseCase indexDocument,
            UpdateSearchDocumentUseCase updateDocument,
            RetireSearchDocumentUseCase retireDocument,
            GetSearchDocumentUseCase getDocument,
            QuerySearchDocumentsUseCase queryDocuments) {
        this.indexDocument = indexDocument;
        this.updateDocument = updateDocument;
        this.retireDocument = retireDocument;
        this.getDocument = getDocument;
        this.queryDocuments = queryDocuments;
    }

    @PostMapping("/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentIdResponse index(@Valid @RequestBody IndexRequest request) {
        return new DocumentIdResponse(indexDocument
                .execute(new IndexSearchDocumentUseCase.Command(
                        request.tenantId(),
                        request.sourceType(),
                        request.sourceId(),
                        request.title(),
                        request.summary(),
                        request.bodyText(),
                        request.locale(),
                        request.tagsJson()))
                .value());
    }

    @PutMapping("/documents/{searchDocumentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable String searchDocumentId, @Valid @RequestBody UpdateRequest request) {
        updateDocument.execute(new UpdateSearchDocumentUseCase.Command(
                searchDocumentId,
                request.title(),
                request.summary(),
                request.bodyText(),
                request.locale(),
                request.tagsJson()));
    }

    @PostMapping("/documents/{searchDocumentId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(
            @PathVariable String searchDocumentId, @Valid @RequestBody ReasonRequest request) {
        retireDocument.execute(searchDocumentId, request.reason());
    }

    @GetMapping("/documents/lookup")
    public DocumentResponse lookup(@RequestParam String tenantId, @RequestParam String documentNumber) {
        return DocumentResponse.from(getDocument.byNumber(tenantId, documentNumber));
    }

    @GetMapping("/documents/{searchDocumentId}")
    public DocumentResponse get(@PathVariable String searchDocumentId) {
        return DocumentResponse.from(getDocument.byId(searchDocumentId));
    }

    @GetMapping
    public List<HitResponse> search(
            @RequestParam String tenantId,
            @RequestParam String q,
            @RequestParam(required = false) String sourceTypes,
            @RequestParam(required = false) Integer limit) {
        return queryDocuments.execute(tenantId, q, sourceTypes, limit).stream()
                .map(HitResponse::from)
                .toList();
    }

    public record IndexRequest(
            @NotBlank String tenantId,
            @NotBlank String sourceType,
            @NotBlank String sourceId,
            @NotBlank @Size(max = 300) String title,
            @Size(max = 1000) String summary,
            @NotBlank String bodyText,
            String locale,
            String tagsJson) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 300) String title,
            @Size(max = 1000) String summary,
            @NotBlank String bodyText,
            @NotBlank String locale,
            String tagsJson) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DocumentIdResponse(String searchDocumentId) {}

    public record DocumentResponse(
            String searchDocumentId,
            String tenantId,
            String documentNumber,
            String sourceType,
            String sourceId,
            String title,
            String summary,
            String bodyText,
            String locale,
            String tagsJson,
            String status) {

        static DocumentResponse from(SearchDocument document) {
            return new DocumentResponse(
                    document.id().value(),
                    document.tenantId().value(),
                    document.documentNumber().value(),
                    document.sourceType().name(),
                    document.sourceId().value(),
                    document.title(),
                    document.summary().orElse(null),
                    document.bodyText(),
                    document.locale().value(),
                    document.tagsJson(),
                    document.status().name());
        }
    }

    public record HitResponse(double score, DocumentResponse document) {
        static HitResponse from(SearchHit hit) {
            return new HitResponse(hit.score(), DocumentResponse.from(hit.document()));
        }
    }
}
