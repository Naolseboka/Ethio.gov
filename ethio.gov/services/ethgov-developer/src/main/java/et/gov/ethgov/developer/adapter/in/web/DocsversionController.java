package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.docsversion.ActivateDocsversionUseCase;
import et.gov.ethgov.developer.application.docsversion.CreateDocsversionUseCase;
import et.gov.ethgov.developer.application.docsversion.GetDocsversionUseCase;
import et.gov.ethgov.developer.application.docsversion.ListDocsversionUseCase;
import et.gov.ethgov.developer.application.docsversion.SuspendDocsversionUseCase;
import et.gov.ethgov.developer.application.docsversion.UpdateDocsversionUseCase;
import et.gov.ethgov.developer.domain.docsversion.Docsversion;
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
@RequestMapping("/v1/developer/docsversion")
public class DocsversionController {
    private final CreateDocsversionUseCase createUseCase;
    private final UpdateDocsversionUseCase updateUseCase;
    private final ActivateDocsversionUseCase activateUseCase;
    private final SuspendDocsversionUseCase suspendUseCase;
    private final GetDocsversionUseCase getUseCase;
    private final ListDocsversionUseCase listUseCase;

    public DocsversionController(
            CreateDocsversionUseCase createUseCase,
            UpdateDocsversionUseCase updateUseCase,
            ActivateDocsversionUseCase activateUseCase,
            SuspendDocsversionUseCase suspendUseCase,
            GetDocsversionUseCase getUseCase,
            ListDocsversionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocsversionIdResponse create(@Valid @RequestBody CreateDocsversionRequest request) {
        var id = createUseCase.execute(new CreateDocsversionUseCase.CreateDocsversionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DocsversionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DocsversionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DocsversionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DocsversionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDocsversionRequest request) {
        var updated = updateUseCase.execute(new UpdateDocsversionUseCase.UpdateDocsversionCommand(
                null, request.title(), request.description(), null, id, null));
        return new DocsversionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDocsversionUseCase.ActivateDocsversionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDocsversionRequest request) {
        suspendUseCase.execute(new SuspendDocsversionUseCase.SuspendDocsversionCommand(
                null, null, null, null, id, request.reason()));
    }

    private DocsversionResponse toResponse(Docsversion aggregate) {
        return new DocsversionResponse(
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

    public record CreateDocsversionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDocsversionRequest(String title, String description) {}

    public record SuspendDocsversionRequest(@NotBlank String reason) {}

    public record DocsversionIdResponse(String id) {}

    public record DocsversionResponse(
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
