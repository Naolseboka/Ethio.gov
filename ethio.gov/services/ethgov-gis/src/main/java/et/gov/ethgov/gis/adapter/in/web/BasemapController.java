package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.basemap.ActivateBasemapUseCase;
import et.gov.ethgov.gis.application.basemap.CreateBasemapUseCase;
import et.gov.ethgov.gis.application.basemap.GetBasemapUseCase;
import et.gov.ethgov.gis.application.basemap.ListBasemapUseCase;
import et.gov.ethgov.gis.application.basemap.SuspendBasemapUseCase;
import et.gov.ethgov.gis.application.basemap.UpdateBasemapUseCase;
import et.gov.ethgov.gis.domain.basemap.Basemap;
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
@RequestMapping("/v1/gis/basemap")
public class BasemapController {
    private final CreateBasemapUseCase createUseCase;
    private final UpdateBasemapUseCase updateUseCase;
    private final ActivateBasemapUseCase activateUseCase;
    private final SuspendBasemapUseCase suspendUseCase;
    private final GetBasemapUseCase getUseCase;
    private final ListBasemapUseCase listUseCase;

    public BasemapController(
            CreateBasemapUseCase createUseCase,
            UpdateBasemapUseCase updateUseCase,
            ActivateBasemapUseCase activateUseCase,
            SuspendBasemapUseCase suspendUseCase,
            GetBasemapUseCase getUseCase,
            ListBasemapUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BasemapIdResponse create(@Valid @RequestBody CreateBasemapRequest request) {
        var id = createUseCase.execute(new CreateBasemapUseCase.CreateBasemapCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BasemapIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BasemapResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BasemapResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BasemapIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBasemapRequest request) {
        var updated = updateUseCase.execute(new UpdateBasemapUseCase.UpdateBasemapCommand(
                null, request.title(), request.description(), null, id, null));
        return new BasemapIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBasemapUseCase.ActivateBasemapCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBasemapRequest request) {
        suspendUseCase.execute(new SuspendBasemapUseCase.SuspendBasemapCommand(
                null, null, null, null, id, request.reason()));
    }

    private BasemapResponse toResponse(Basemap aggregate) {
        return new BasemapResponse(
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

    public record CreateBasemapRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBasemapRequest(String title, String description) {}

    public record SuspendBasemapRequest(@NotBlank String reason) {}

    public record BasemapIdResponse(String id) {}

    public record BasemapResponse(
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
