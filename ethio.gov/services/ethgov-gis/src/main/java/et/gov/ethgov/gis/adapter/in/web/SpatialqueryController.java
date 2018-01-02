package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.spatialquery.ActivateSpatialqueryUseCase;
import et.gov.ethgov.gis.application.spatialquery.CreateSpatialqueryUseCase;
import et.gov.ethgov.gis.application.spatialquery.GetSpatialqueryUseCase;
import et.gov.ethgov.gis.application.spatialquery.ListSpatialqueryUseCase;
import et.gov.ethgov.gis.application.spatialquery.SuspendSpatialqueryUseCase;
import et.gov.ethgov.gis.application.spatialquery.UpdateSpatialqueryUseCase;
import et.gov.ethgov.gis.domain.spatialquery.Spatialquery;
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
@RequestMapping("/v1/gis/spatialquery")
public class SpatialqueryController {
    private final CreateSpatialqueryUseCase createUseCase;
    private final UpdateSpatialqueryUseCase updateUseCase;
    private final ActivateSpatialqueryUseCase activateUseCase;
    private final SuspendSpatialqueryUseCase suspendUseCase;
    private final GetSpatialqueryUseCase getUseCase;
    private final ListSpatialqueryUseCase listUseCase;

    public SpatialqueryController(
            CreateSpatialqueryUseCase createUseCase,
            UpdateSpatialqueryUseCase updateUseCase,
            ActivateSpatialqueryUseCase activateUseCase,
            SuspendSpatialqueryUseCase suspendUseCase,
            GetSpatialqueryUseCase getUseCase,
            ListSpatialqueryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SpatialqueryIdResponse create(@Valid @RequestBody CreateSpatialqueryRequest request) {
        var id = createUseCase.execute(new CreateSpatialqueryUseCase.CreateSpatialqueryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SpatialqueryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SpatialqueryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SpatialqueryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SpatialqueryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSpatialqueryRequest request) {
        var updated = updateUseCase.execute(new UpdateSpatialqueryUseCase.UpdateSpatialqueryCommand(
                null, request.title(), request.description(), null, id, null));
        return new SpatialqueryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSpatialqueryUseCase.ActivateSpatialqueryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSpatialqueryRequest request) {
        suspendUseCase.execute(new SuspendSpatialqueryUseCase.SuspendSpatialqueryCommand(
                null, null, null, null, id, request.reason()));
    }

    private SpatialqueryResponse toResponse(Spatialquery aggregate) {
        return new SpatialqueryResponse(
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

    public record CreateSpatialqueryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSpatialqueryRequest(String title, String description) {}

    public record SuspendSpatialqueryRequest(@NotBlank String reason) {}

    public record SpatialqueryIdResponse(String id) {}

    public record SpatialqueryResponse(
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
