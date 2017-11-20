package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.documentset.ActivateDocumentsetUseCase;
import et.gov.ethgov.document.application.documentset.CreateDocumentsetUseCase;
import et.gov.ethgov.document.application.documentset.GetDocumentsetUseCase;
import et.gov.ethgov.document.application.documentset.ListDocumentsetUseCase;
import et.gov.ethgov.document.application.documentset.SuspendDocumentsetUseCase;
import et.gov.ethgov.document.application.documentset.UpdateDocumentsetUseCase;
import et.gov.ethgov.document.domain.documentset.Documentset;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/v1/document/documentset")
public class DocumentsetController {
    private final CreateDocumentsetUseCase createUseCase;
    private final UpdateDocumentsetUseCase updateUseCase;
    private final ActivateDocumentsetUseCase activateUseCase;
    private final SuspendDocumentsetUseCase suspendUseCase;
    private final GetDocumentsetUseCase getUseCase;
    private final ListDocumentsetUseCase listUseCase;

    public DocumentsetController(
            CreateDocumentsetUseCase createUseCase,
            UpdateDocumentsetUseCase updateUseCase,
            ActivateDocumentsetUseCase activateUseCase,
            SuspendDocumentsetUseCase suspendUseCase,
            GetDocumentsetUseCase getUseCase,
            ListDocumentsetUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentsetIdResponse create(@Valid @RequestBody CreateDocumentsetRequest request) {
        var id = createUseCase.execute(new CreateDocumentsetUseCase.CreateDocumentsetCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DocumentsetIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DocumentsetResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DocumentsetResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DocumentsetIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDocumentsetRequest request) {
        var updated = updateUseCase.execute(new UpdateDocumentsetUseCase.UpdateDocumentsetCommand(
                null, request.title(), request.description(), null, id, null));
        return new DocumentsetIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDocumentsetUseCase.ActivateDocumentsetCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDocumentsetRequest request) {
        suspendUseCase.execute(new SuspendDocumentsetUseCase.SuspendDocumentsetCommand(
                null, null, null, null, id, request.reason()));
    }

    private DocumentsetResponse toResponse(Documentset aggregate) {
        return new DocumentsetResponse(
                aggregate.id().toString(),
                aggregate.referenceCode(),
                aggregate.title(),
                aggregate.description(),
                aggregate.tenantId(),
                aggregate.status().name(),
                aggregate.createdAt().toString(),
                aggregate.updatedAt().toString(),
                aggregate.version());
    }

    public record CreateDocumentsetRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDocumentsetRequest(String title, String description) {}

    public record SuspendDocumentsetRequest(@NotBlank String reason) {}

    public record DocumentsetIdResponse(String id) {}

    public record DocumentsetResponse(
            String id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            String createdAt,
            String updatedAt,
            long version) {}
}
