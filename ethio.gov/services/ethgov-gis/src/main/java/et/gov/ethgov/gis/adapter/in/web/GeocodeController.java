package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.geocode.ActivateGeocodeUseCase;
import et.gov.ethgov.gis.application.geocode.CreateGeocodeUseCase;
import et.gov.ethgov.gis.application.geocode.GetGeocodeUseCase;
import et.gov.ethgov.gis.application.geocode.ListGeocodeUseCase;
import et.gov.ethgov.gis.application.geocode.SuspendGeocodeUseCase;
import et.gov.ethgov.gis.application.geocode.UpdateGeocodeUseCase;
import et.gov.ethgov.gis.domain.geocode.Geocode;
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
@RequestMapping("/v1/gis/geocode")
public class GeocodeController {
    private final CreateGeocodeUseCase createUseCase;
    private final UpdateGeocodeUseCase updateUseCase;
    private final ActivateGeocodeUseCase activateUseCase;
    private final SuspendGeocodeUseCase suspendUseCase;
    private final GetGeocodeUseCase getUseCase;
    private final ListGeocodeUseCase listUseCase;

    public GeocodeController(
            CreateGeocodeUseCase createUseCase,
            UpdateGeocodeUseCase updateUseCase,
            ActivateGeocodeUseCase activateUseCase,
            SuspendGeocodeUseCase suspendUseCase,
            GetGeocodeUseCase getUseCase,
            ListGeocodeUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeocodeIdResponse create(@Valid @RequestBody CreateGeocodeRequest request) {
        var id = createUseCase.execute(new CreateGeocodeUseCase.CreateGeocodeCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new GeocodeIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public GeocodeResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<GeocodeResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public GeocodeIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateGeocodeRequest request) {
        var updated = updateUseCase.execute(new UpdateGeocodeUseCase.UpdateGeocodeCommand(
                null, request.title(), request.description(), null, id, null));
        return new GeocodeIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateGeocodeUseCase.ActivateGeocodeCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendGeocodeRequest request) {
        suspendUseCase.execute(new SuspendGeocodeUseCase.SuspendGeocodeCommand(
                null, null, null, null, id, request.reason()));
    }

    private GeocodeResponse toResponse(Geocode aggregate) {
        return new GeocodeResponse(
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

    public record CreateGeocodeRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateGeocodeRequest(String title, String description) {}

    public record SuspendGeocodeRequest(@NotBlank String reason) {}

    public record GeocodeIdResponse(String id) {}

    public record GeocodeResponse(
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
