package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.redaction.ActivateRedactionUseCase;
import et.gov.ethgov.document.application.redaction.CreateRedactionUseCase;
import et.gov.ethgov.document.application.redaction.GetRedactionUseCase;
import et.gov.ethgov.document.application.redaction.ListRedactionUseCase;
import et.gov.ethgov.document.application.redaction.SuspendRedactionUseCase;
import et.gov.ethgov.document.application.redaction.UpdateRedactionUseCase;
import et.gov.ethgov.document.domain.redaction.Redaction;
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
@RequestMapping("/v1/document/redaction")
public class RedactionController {
    private final CreateRedactionUseCase createUseCase;
    private final UpdateRedactionUseCase updateUseCase;
    private final ActivateRedactionUseCase activateUseCase;
    private final SuspendRedactionUseCase suspendUseCase;
    private final GetRedactionUseCase getUseCase;
    private final ListRedactionUseCase listUseCase;

    public RedactionController(
            CreateRedactionUseCase createUseCase,
            UpdateRedactionUseCase updateUseCase,
            ActivateRedactionUseCase activateUseCase,
            SuspendRedactionUseCase suspendUseCase,
            GetRedactionUseCase getUseCase,
            ListRedactionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RedactionIdResponse create(@Valid @RequestBody CreateRedactionRequest request) {
        var id = createUseCase.execute(new CreateRedactionUseCase.CreateRedactionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RedactionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RedactionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RedactionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RedactionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRedactionRequest request) {
        var updated = updateUseCase.execute(new UpdateRedactionUseCase.UpdateRedactionCommand(
                null, request.title(), request.description(), null, id, null));
        return new RedactionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRedactionUseCase.ActivateRedactionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRedactionRequest request) {
        suspendUseCase.execute(new SuspendRedactionUseCase.SuspendRedactionCommand(
                null, null, null, null, id, request.reason()));
    }

    private RedactionResponse toResponse(Redaction aggregate) {
        return new RedactionResponse(
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

    public record CreateRedactionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRedactionRequest(String title, String description) {}

    public record SuspendRedactionRequest(@NotBlank String reason) {}

    public record RedactionIdResponse(String id) {}

    public record RedactionResponse(
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
