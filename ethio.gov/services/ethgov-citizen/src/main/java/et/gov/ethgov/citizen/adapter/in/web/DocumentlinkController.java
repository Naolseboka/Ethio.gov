package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.documentlink.ActivateDocumentlinkUseCase;
import et.gov.ethgov.citizen.application.documentlink.CreateDocumentlinkUseCase;
import et.gov.ethgov.citizen.application.documentlink.GetDocumentlinkUseCase;
import et.gov.ethgov.citizen.application.documentlink.ListDocumentlinkUseCase;
import et.gov.ethgov.citizen.application.documentlink.SuspendDocumentlinkUseCase;
import et.gov.ethgov.citizen.application.documentlink.UpdateDocumentlinkUseCase;
import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
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
@RequestMapping("/v1/citizen/documentlink")
public class DocumentlinkController {
    private final CreateDocumentlinkUseCase createUseCase;
    private final UpdateDocumentlinkUseCase updateUseCase;
    private final ActivateDocumentlinkUseCase activateUseCase;
    private final SuspendDocumentlinkUseCase suspendUseCase;
    private final GetDocumentlinkUseCase getUseCase;
    private final ListDocumentlinkUseCase listUseCase;

    public DocumentlinkController(
            CreateDocumentlinkUseCase createUseCase,
            UpdateDocumentlinkUseCase updateUseCase,
            ActivateDocumentlinkUseCase activateUseCase,
            SuspendDocumentlinkUseCase suspendUseCase,
            GetDocumentlinkUseCase getUseCase,
            ListDocumentlinkUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentlinkIdResponse create(@Valid @RequestBody CreateDocumentlinkRequest request) {
        var id = createUseCase.execute(new CreateDocumentlinkUseCase.CreateDocumentlinkCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DocumentlinkIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DocumentlinkResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DocumentlinkResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DocumentlinkIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDocumentlinkRequest request) {
        var updated = updateUseCase.execute(new UpdateDocumentlinkUseCase.UpdateDocumentlinkCommand(
                null, request.title(), request.description(), null, id, null));
        return new DocumentlinkIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDocumentlinkUseCase.ActivateDocumentlinkCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDocumentlinkRequest request) {
        suspendUseCase.execute(new SuspendDocumentlinkUseCase.SuspendDocumentlinkCommand(
                null, null, null, null, id, request.reason()));
    }

    private DocumentlinkResponse toResponse(Documentlink aggregate) {
        return new DocumentlinkResponse(
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

    public record CreateDocumentlinkRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDocumentlinkRequest(String title, String description) {}

    public record SuspendDocumentlinkRequest(@NotBlank String reason) {}

    public record DocumentlinkIdResponse(String id) {}

    public record DocumentlinkResponse(
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
