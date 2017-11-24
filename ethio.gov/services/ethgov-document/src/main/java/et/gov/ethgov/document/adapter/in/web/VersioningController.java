package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.versioning.ActivateVersioningUseCase;
import et.gov.ethgov.document.application.versioning.CreateVersioningUseCase;
import et.gov.ethgov.document.application.versioning.GetVersioningUseCase;
import et.gov.ethgov.document.application.versioning.ListVersioningUseCase;
import et.gov.ethgov.document.application.versioning.SuspendVersioningUseCase;
import et.gov.ethgov.document.application.versioning.UpdateVersioningUseCase;
import et.gov.ethgov.document.domain.versioning.Versioning;
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
@RequestMapping("/v1/document/versioning")
public class VersioningController {
    private final CreateVersioningUseCase createUseCase;
    private final UpdateVersioningUseCase updateUseCase;
    private final ActivateVersioningUseCase activateUseCase;
    private final SuspendVersioningUseCase suspendUseCase;
    private final GetVersioningUseCase getUseCase;
    private final ListVersioningUseCase listUseCase;

    public VersioningController(
            CreateVersioningUseCase createUseCase,
            UpdateVersioningUseCase updateUseCase,
            ActivateVersioningUseCase activateUseCase,
            SuspendVersioningUseCase suspendUseCase,
            GetVersioningUseCase getUseCase,
            ListVersioningUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VersioningIdResponse create(@Valid @RequestBody CreateVersioningRequest request) {
        var id = createUseCase.execute(new CreateVersioningUseCase.CreateVersioningCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new VersioningIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public VersioningResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<VersioningResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public VersioningIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateVersioningRequest request) {
        var updated = updateUseCase.execute(new UpdateVersioningUseCase.UpdateVersioningCommand(
                null, request.title(), request.description(), null, id, null));
        return new VersioningIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateVersioningUseCase.ActivateVersioningCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendVersioningRequest request) {
        suspendUseCase.execute(new SuspendVersioningUseCase.SuspendVersioningCommand(
                null, null, null, null, id, request.reason()));
    }

    private VersioningResponse toResponse(Versioning aggregate) {
        return new VersioningResponse(
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

    public record CreateVersioningRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateVersioningRequest(String title, String description) {}

    public record SuspendVersioningRequest(@NotBlank String reason) {}

    public record VersioningIdResponse(String id) {}

    public record VersioningResponse(
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
