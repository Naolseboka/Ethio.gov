package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.instance.ActivateInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.CreateInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.GetInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.ListInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.SuspendInstanceUseCase;
import et.gov.ethgov.workflow.application.instance.UpdateInstanceUseCase;
import et.gov.ethgov.workflow.domain.instance.Instance;
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
@RequestMapping("/v1/workflow/instance")
public class InstanceController {
    private final CreateInstanceUseCase createUseCase;
    private final UpdateInstanceUseCase updateUseCase;
    private final ActivateInstanceUseCase activateUseCase;
    private final SuspendInstanceUseCase suspendUseCase;
    private final GetInstanceUseCase getUseCase;
    private final ListInstanceUseCase listUseCase;

    public InstanceController(
            CreateInstanceUseCase createUseCase,
            UpdateInstanceUseCase updateUseCase,
            ActivateInstanceUseCase activateUseCase,
            SuspendInstanceUseCase suspendUseCase,
            GetInstanceUseCase getUseCase,
            ListInstanceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstanceIdResponse create(@Valid @RequestBody CreateInstanceRequest request) {
        var id = createUseCase.execute(new CreateInstanceUseCase.CreateInstanceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new InstanceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public InstanceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<InstanceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public InstanceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateInstanceRequest request) {
        var updated = updateUseCase.execute(new UpdateInstanceUseCase.UpdateInstanceCommand(
                null, request.title(), request.description(), null, id, null));
        return new InstanceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateInstanceUseCase.ActivateInstanceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendInstanceRequest request) {
        suspendUseCase.execute(new SuspendInstanceUseCase.SuspendInstanceCommand(
                null, null, null, null, id, request.reason()));
    }

    private InstanceResponse toResponse(Instance aggregate) {
        return new InstanceResponse(
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

    public record CreateInstanceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateInstanceRequest(String title, String description) {}

    public record SuspendInstanceRequest(@NotBlank String reason) {}

    public record InstanceIdResponse(String id) {}

    public record InstanceResponse(
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
