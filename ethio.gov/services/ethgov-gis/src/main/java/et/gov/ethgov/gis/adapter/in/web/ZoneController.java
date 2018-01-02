package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.zone.ActivateZoneUseCase;
import et.gov.ethgov.gis.application.zone.CreateZoneUseCase;
import et.gov.ethgov.gis.application.zone.GetZoneUseCase;
import et.gov.ethgov.gis.application.zone.ListZoneUseCase;
import et.gov.ethgov.gis.application.zone.SuspendZoneUseCase;
import et.gov.ethgov.gis.application.zone.UpdateZoneUseCase;
import et.gov.ethgov.gis.domain.zone.Zone;
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
@RequestMapping("/v1/gis/zone")
public class ZoneController {
    private final CreateZoneUseCase createUseCase;
    private final UpdateZoneUseCase updateUseCase;
    private final ActivateZoneUseCase activateUseCase;
    private final SuspendZoneUseCase suspendUseCase;
    private final GetZoneUseCase getUseCase;
    private final ListZoneUseCase listUseCase;

    public ZoneController(
            CreateZoneUseCase createUseCase,
            UpdateZoneUseCase updateUseCase,
            ActivateZoneUseCase activateUseCase,
            SuspendZoneUseCase suspendUseCase,
            GetZoneUseCase getUseCase,
            ListZoneUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ZoneIdResponse create(@Valid @RequestBody CreateZoneRequest request) {
        var id = createUseCase.execute(new CreateZoneUseCase.CreateZoneCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ZoneIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ZoneResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ZoneResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ZoneIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateZoneRequest request) {
        var updated = updateUseCase.execute(new UpdateZoneUseCase.UpdateZoneCommand(
                null, request.title(), request.description(), null, id, null));
        return new ZoneIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateZoneUseCase.ActivateZoneCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendZoneRequest request) {
        suspendUseCase.execute(new SuspendZoneUseCase.SuspendZoneCommand(
                null, null, null, null, id, request.reason()));
    }

    private ZoneResponse toResponse(Zone aggregate) {
        return new ZoneResponse(
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

    public record CreateZoneRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateZoneRequest(String title, String description) {}

    public record SuspendZoneRequest(@NotBlank String reason) {}

    public record ZoneIdResponse(String id) {}

    public record ZoneResponse(
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
