package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.gisexport.ActivateGisexportUseCase;
import et.gov.ethgov.gis.application.gisexport.CreateGisexportUseCase;
import et.gov.ethgov.gis.application.gisexport.GetGisexportUseCase;
import et.gov.ethgov.gis.application.gisexport.ListGisexportUseCase;
import et.gov.ethgov.gis.application.gisexport.SuspendGisexportUseCase;
import et.gov.ethgov.gis.application.gisexport.UpdateGisexportUseCase;
import et.gov.ethgov.gis.domain.gisexport.Gisexport;
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
@RequestMapping("/v1/gis/gisexport")
public class GisexportController {
    private final CreateGisexportUseCase createUseCase;
    private final UpdateGisexportUseCase updateUseCase;
    private final ActivateGisexportUseCase activateUseCase;
    private final SuspendGisexportUseCase suspendUseCase;
    private final GetGisexportUseCase getUseCase;
    private final ListGisexportUseCase listUseCase;

    public GisexportController(
            CreateGisexportUseCase createUseCase,
            UpdateGisexportUseCase updateUseCase,
            ActivateGisexportUseCase activateUseCase,
            SuspendGisexportUseCase suspendUseCase,
            GetGisexportUseCase getUseCase,
            ListGisexportUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GisexportIdResponse create(@Valid @RequestBody CreateGisexportRequest request) {
        var id = createUseCase.execute(new CreateGisexportUseCase.CreateGisexportCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new GisexportIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public GisexportResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<GisexportResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public GisexportIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateGisexportRequest request) {
        var updated = updateUseCase.execute(new UpdateGisexportUseCase.UpdateGisexportCommand(
                null, request.title(), request.description(), null, id, null));
        return new GisexportIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateGisexportUseCase.ActivateGisexportCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendGisexportRequest request) {
        suspendUseCase.execute(new SuspendGisexportUseCase.SuspendGisexportCommand(
                null, null, null, null, id, request.reason()));
    }

    private GisexportResponse toResponse(Gisexport aggregate) {
        return new GisexportResponse(
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

    public record CreateGisexportRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateGisexportRequest(String title, String description) {}

    public record SuspendGisexportRequest(@NotBlank String reason) {}

    public record GisexportIdResponse(String id) {}

    public record GisexportResponse(
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
