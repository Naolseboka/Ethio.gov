package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.extension.ActivateExtensionUseCase;
import et.gov.ethgov.permits.application.extension.CreateExtensionUseCase;
import et.gov.ethgov.permits.application.extension.GetExtensionUseCase;
import et.gov.ethgov.permits.application.extension.ListExtensionUseCase;
import et.gov.ethgov.permits.application.extension.SuspendExtensionUseCase;
import et.gov.ethgov.permits.application.extension.UpdateExtensionUseCase;
import et.gov.ethgov.permits.domain.extension.Extension;
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
@RequestMapping("/v1/permits/extension")
public class ExtensionController {
    private final CreateExtensionUseCase createUseCase;
    private final UpdateExtensionUseCase updateUseCase;
    private final ActivateExtensionUseCase activateUseCase;
    private final SuspendExtensionUseCase suspendUseCase;
    private final GetExtensionUseCase getUseCase;
    private final ListExtensionUseCase listUseCase;

    public ExtensionController(
            CreateExtensionUseCase createUseCase,
            UpdateExtensionUseCase updateUseCase,
            ActivateExtensionUseCase activateUseCase,
            SuspendExtensionUseCase suspendUseCase,
            GetExtensionUseCase getUseCase,
            ListExtensionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExtensionIdResponse create(@Valid @RequestBody CreateExtensionRequest request) {
        var id = createUseCase.execute(new CreateExtensionUseCase.CreateExtensionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ExtensionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ExtensionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ExtensionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ExtensionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateExtensionRequest request) {
        var updated = updateUseCase.execute(new UpdateExtensionUseCase.UpdateExtensionCommand(
                null, request.title(), request.description(), null, id, null));
        return new ExtensionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateExtensionUseCase.ActivateExtensionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendExtensionRequest request) {
        suspendUseCase.execute(new SuspendExtensionUseCase.SuspendExtensionCommand(
                null, null, null, null, id, request.reason()));
    }

    private ExtensionResponse toResponse(Extension aggregate) {
        return new ExtensionResponse(
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

    public record CreateExtensionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateExtensionRequest(String title, String description) {}

    public record SuspendExtensionRequest(@NotBlank String reason) {}

    public record ExtensionIdResponse(String id) {}

    public record ExtensionResponse(
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
