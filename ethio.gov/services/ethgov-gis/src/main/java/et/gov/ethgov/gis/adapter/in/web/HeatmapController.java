package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.heatmap.ActivateHeatmapUseCase;
import et.gov.ethgov.gis.application.heatmap.CreateHeatmapUseCase;
import et.gov.ethgov.gis.application.heatmap.GetHeatmapUseCase;
import et.gov.ethgov.gis.application.heatmap.ListHeatmapUseCase;
import et.gov.ethgov.gis.application.heatmap.SuspendHeatmapUseCase;
import et.gov.ethgov.gis.application.heatmap.UpdateHeatmapUseCase;
import et.gov.ethgov.gis.domain.heatmap.Heatmap;
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
@RequestMapping("/v1/gis/heatmap")
public class HeatmapController {
    private final CreateHeatmapUseCase createUseCase;
    private final UpdateHeatmapUseCase updateUseCase;
    private final ActivateHeatmapUseCase activateUseCase;
    private final SuspendHeatmapUseCase suspendUseCase;
    private final GetHeatmapUseCase getUseCase;
    private final ListHeatmapUseCase listUseCase;

    public HeatmapController(
            CreateHeatmapUseCase createUseCase,
            UpdateHeatmapUseCase updateUseCase,
            ActivateHeatmapUseCase activateUseCase,
            SuspendHeatmapUseCase suspendUseCase,
            GetHeatmapUseCase getUseCase,
            ListHeatmapUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HeatmapIdResponse create(@Valid @RequestBody CreateHeatmapRequest request) {
        var id = createUseCase.execute(new CreateHeatmapUseCase.CreateHeatmapCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HeatmapIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HeatmapResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HeatmapResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HeatmapIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHeatmapRequest request) {
        var updated = updateUseCase.execute(new UpdateHeatmapUseCase.UpdateHeatmapCommand(
                null, request.title(), request.description(), null, id, null));
        return new HeatmapIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHeatmapUseCase.ActivateHeatmapCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHeatmapRequest request) {
        suspendUseCase.execute(new SuspendHeatmapUseCase.SuspendHeatmapCommand(
                null, null, null, null, id, request.reason()));
    }

    private HeatmapResponse toResponse(Heatmap aggregate) {
        return new HeatmapResponse(
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

    public record CreateHeatmapRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHeatmapRequest(String title, String description) {}

    public record SuspendHeatmapRequest(@NotBlank String reason) {}

    public record HeatmapIdResponse(String id) {}

    public record HeatmapResponse(
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
