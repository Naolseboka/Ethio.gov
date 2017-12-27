package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.layer.ActivateLayerUseCase;
import et.gov.ethgov.gis.application.layer.CreateLayerUseCase;
import et.gov.ethgov.gis.application.layer.GetLayerUseCase;
import et.gov.ethgov.gis.application.layer.ListLayerUseCase;
import et.gov.ethgov.gis.application.layer.SuspendLayerUseCase;
import et.gov.ethgov.gis.application.layer.UpdateLayerUseCase;
import et.gov.ethgov.gis.domain.layer.Layer;
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
@RequestMapping("/v1/gis/layer")
public class LayerController {
    private final CreateLayerUseCase createUseCase;
    private final UpdateLayerUseCase updateUseCase;
    private final ActivateLayerUseCase activateUseCase;
    private final SuspendLayerUseCase suspendUseCase;
    private final GetLayerUseCase getUseCase;
    private final ListLayerUseCase listUseCase;

    public LayerController(
            CreateLayerUseCase createUseCase,
            UpdateLayerUseCase updateUseCase,
            ActivateLayerUseCase activateUseCase,
            SuspendLayerUseCase suspendUseCase,
            GetLayerUseCase getUseCase,
            ListLayerUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LayerIdResponse create(@Valid @RequestBody CreateLayerRequest request) {
        var id = createUseCase.execute(new CreateLayerUseCase.CreateLayerCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new LayerIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public LayerResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<LayerResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public LayerIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateLayerRequest request) {
        var updated = updateUseCase.execute(new UpdateLayerUseCase.UpdateLayerCommand(
                null, request.title(), request.description(), null, id, null));
        return new LayerIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateLayerUseCase.ActivateLayerCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendLayerRequest request) {
        suspendUseCase.execute(new SuspendLayerUseCase.SuspendLayerCommand(
                null, null, null, null, id, request.reason()));
    }

    private LayerResponse toResponse(Layer aggregate) {
        return new LayerResponse(
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

    public record CreateLayerRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateLayerRequest(String title, String description) {}

    public record SuspendLayerRequest(@NotBlank String reason) {}

    public record LayerIdResponse(String id) {}

    public record LayerResponse(
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
