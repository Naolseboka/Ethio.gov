package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.occupancy.ActivateOccupancyUseCase;
import et.gov.ethgov.permits.application.occupancy.CreateOccupancyUseCase;
import et.gov.ethgov.permits.application.occupancy.GetOccupancyUseCase;
import et.gov.ethgov.permits.application.occupancy.ListOccupancyUseCase;
import et.gov.ethgov.permits.application.occupancy.SuspendOccupancyUseCase;
import et.gov.ethgov.permits.application.occupancy.UpdateOccupancyUseCase;
import et.gov.ethgov.permits.domain.occupancy.Occupancy;
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
@RequestMapping("/v1/permits/occupancy")
public class OccupancyController {
    private final CreateOccupancyUseCase createUseCase;
    private final UpdateOccupancyUseCase updateUseCase;
    private final ActivateOccupancyUseCase activateUseCase;
    private final SuspendOccupancyUseCase suspendUseCase;
    private final GetOccupancyUseCase getUseCase;
    private final ListOccupancyUseCase listUseCase;

    public OccupancyController(
            CreateOccupancyUseCase createUseCase,
            UpdateOccupancyUseCase updateUseCase,
            ActivateOccupancyUseCase activateUseCase,
            SuspendOccupancyUseCase suspendUseCase,
            GetOccupancyUseCase getUseCase,
            ListOccupancyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OccupancyIdResponse create(@Valid @RequestBody CreateOccupancyRequest request) {
        var id = createUseCase.execute(new CreateOccupancyUseCase.CreateOccupancyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new OccupancyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public OccupancyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<OccupancyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public OccupancyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateOccupancyRequest request) {
        var updated = updateUseCase.execute(new UpdateOccupancyUseCase.UpdateOccupancyCommand(
                null, request.title(), request.description(), null, id, null));
        return new OccupancyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateOccupancyUseCase.ActivateOccupancyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendOccupancyRequest request) {
        suspendUseCase.execute(new SuspendOccupancyUseCase.SuspendOccupancyCommand(
                null, null, null, null, id, request.reason()));
    }

    private OccupancyResponse toResponse(Occupancy aggregate) {
        return new OccupancyResponse(
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

    public record CreateOccupancyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateOccupancyRequest(String title, String description) {}

    public record SuspendOccupancyRequest(@NotBlank String reason) {}

    public record OccupancyIdResponse(String id) {}

    public record OccupancyResponse(
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
