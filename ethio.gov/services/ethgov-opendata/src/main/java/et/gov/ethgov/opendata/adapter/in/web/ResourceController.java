package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.resource.ActivateResourceUseCase;
import et.gov.ethgov.opendata.application.resource.CreateResourceUseCase;
import et.gov.ethgov.opendata.application.resource.GetResourceUseCase;
import et.gov.ethgov.opendata.application.resource.ListResourceUseCase;
import et.gov.ethgov.opendata.application.resource.SuspendResourceUseCase;
import et.gov.ethgov.opendata.application.resource.UpdateResourceUseCase;
import et.gov.ethgov.opendata.domain.resource.Resource;
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
@RequestMapping("/v1/opendata/resource")
public class ResourceController {
    private final CreateResourceUseCase createUseCase;
    private final UpdateResourceUseCase updateUseCase;
    private final ActivateResourceUseCase activateUseCase;
    private final SuspendResourceUseCase suspendUseCase;
    private final GetResourceUseCase getUseCase;
    private final ListResourceUseCase listUseCase;

    public ResourceController(
            CreateResourceUseCase createUseCase,
            UpdateResourceUseCase updateUseCase,
            ActivateResourceUseCase activateUseCase,
            SuspendResourceUseCase suspendUseCase,
            GetResourceUseCase getUseCase,
            ListResourceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceIdResponse create(@Valid @RequestBody CreateResourceRequest request) {
        var id = createUseCase.execute(new CreateResourceUseCase.CreateResourceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ResourceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ResourceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ResourceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ResourceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateResourceRequest request) {
        var updated = updateUseCase.execute(new UpdateResourceUseCase.UpdateResourceCommand(
                null, request.title(), request.description(), null, id, null));
        return new ResourceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateResourceUseCase.ActivateResourceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendResourceRequest request) {
        suspendUseCase.execute(new SuspendResourceUseCase.SuspendResourceCommand(
                null, null, null, null, id, request.reason()));
    }

    private ResourceResponse toResponse(Resource aggregate) {
        return new ResourceResponse(
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

    public record CreateResourceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateResourceRequest(String title, String description) {}

    public record SuspendResourceRequest(@NotBlank String reason) {}

    public record ResourceIdResponse(String id) {}

    public record ResourceResponse(
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
