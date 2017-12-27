package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.overlay.ActivateOverlayUseCase;
import et.gov.ethgov.gis.application.overlay.CreateOverlayUseCase;
import et.gov.ethgov.gis.application.overlay.GetOverlayUseCase;
import et.gov.ethgov.gis.application.overlay.ListOverlayUseCase;
import et.gov.ethgov.gis.application.overlay.SuspendOverlayUseCase;
import et.gov.ethgov.gis.application.overlay.UpdateOverlayUseCase;
import et.gov.ethgov.gis.domain.overlay.Overlay;
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
@RequestMapping("/v1/gis/overlay")
public class OverlayController {
    private final CreateOverlayUseCase createUseCase;
    private final UpdateOverlayUseCase updateUseCase;
    private final ActivateOverlayUseCase activateUseCase;
    private final SuspendOverlayUseCase suspendUseCase;
    private final GetOverlayUseCase getUseCase;
    private final ListOverlayUseCase listUseCase;

    public OverlayController(
            CreateOverlayUseCase createUseCase,
            UpdateOverlayUseCase updateUseCase,
            ActivateOverlayUseCase activateUseCase,
            SuspendOverlayUseCase suspendUseCase,
            GetOverlayUseCase getUseCase,
            ListOverlayUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OverlayIdResponse create(@Valid @RequestBody CreateOverlayRequest request) {
        var id = createUseCase.execute(new CreateOverlayUseCase.CreateOverlayCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OverlayIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OverlayResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OverlayResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OverlayIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOverlayRequest request) {
        var updated = updateUseCase.execute(new UpdateOverlayUseCase.UpdateOverlayCommand(
                null, request.title(), request.description(), null, id, null));
        return new OverlayIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOverlayUseCase.ActivateOverlayCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOverlayRequest request) {
        suspendUseCase.execute(new SuspendOverlayUseCase.SuspendOverlayCommand(
                null, null, null, null, id, request.reason()));
    }

    private OverlayResponse toResponse(Overlay aggregate) {
        return new OverlayResponse(
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

    public record CreateOverlayRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOverlayRequest(String title, String description) {}

    public record SuspendOverlayRequest(@NotBlank String reason) {}

    public record OverlayIdResponse(String id) {}

    public record OverlayResponse(
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
