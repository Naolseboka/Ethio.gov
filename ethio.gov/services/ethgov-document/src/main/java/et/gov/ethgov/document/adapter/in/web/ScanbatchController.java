package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.scanbatch.ActivateScanbatchUseCase;
import et.gov.ethgov.document.application.scanbatch.CreateScanbatchUseCase;
import et.gov.ethgov.document.application.scanbatch.GetScanbatchUseCase;
import et.gov.ethgov.document.application.scanbatch.ListScanbatchUseCase;
import et.gov.ethgov.document.application.scanbatch.SuspendScanbatchUseCase;
import et.gov.ethgov.document.application.scanbatch.UpdateScanbatchUseCase;
import et.gov.ethgov.document.domain.scanbatch.Scanbatch;
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
@RequestMapping("/v1/document/scanbatch")
public class ScanbatchController {
    private final CreateScanbatchUseCase createUseCase;
    private final UpdateScanbatchUseCase updateUseCase;
    private final ActivateScanbatchUseCase activateUseCase;
    private final SuspendScanbatchUseCase suspendUseCase;
    private final GetScanbatchUseCase getUseCase;
    private final ListScanbatchUseCase listUseCase;

    public ScanbatchController(
            CreateScanbatchUseCase createUseCase,
            UpdateScanbatchUseCase updateUseCase,
            ActivateScanbatchUseCase activateUseCase,
            SuspendScanbatchUseCase suspendUseCase,
            GetScanbatchUseCase getUseCase,
            ListScanbatchUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScanbatchIdResponse create(@Valid @RequestBody CreateScanbatchRequest request) {
        var id = createUseCase.execute(new CreateScanbatchUseCase.CreateScanbatchCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ScanbatchIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ScanbatchResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ScanbatchResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ScanbatchIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateScanbatchRequest request) {
        var updated = updateUseCase.execute(new UpdateScanbatchUseCase.UpdateScanbatchCommand(
                null, request.title(), request.description(), null, id, null));
        return new ScanbatchIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateScanbatchUseCase.ActivateScanbatchCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendScanbatchRequest request) {
        suspendUseCase.execute(new SuspendScanbatchUseCase.SuspendScanbatchCommand(
                null, null, null, null, id, request.reason()));
    }

    private ScanbatchResponse toResponse(Scanbatch aggregate) {
        return new ScanbatchResponse(
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

    public record CreateScanbatchRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateScanbatchRequest(String title, String description) {}

    public record SuspendScanbatchRequest(@NotBlank String reason) {}

    public record ScanbatchIdResponse(String id) {}

    public record ScanbatchResponse(
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
