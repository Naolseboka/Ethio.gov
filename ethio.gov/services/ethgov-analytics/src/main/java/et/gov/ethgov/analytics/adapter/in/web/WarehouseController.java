package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.warehouse.ActivateWarehouseUseCase;
import et.gov.ethgov.analytics.application.warehouse.CreateWarehouseUseCase;
import et.gov.ethgov.analytics.application.warehouse.GetWarehouseUseCase;
import et.gov.ethgov.analytics.application.warehouse.ListWarehouseUseCase;
import et.gov.ethgov.analytics.application.warehouse.SuspendWarehouseUseCase;
import et.gov.ethgov.analytics.application.warehouse.UpdateWarehouseUseCase;
import et.gov.ethgov.analytics.domain.warehouse.Warehouse;
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
@RequestMapping("/v1/analytics/warehouse")
public class WarehouseController {
    private final CreateWarehouseUseCase createUseCase;
    private final UpdateWarehouseUseCase updateUseCase;
    private final ActivateWarehouseUseCase activateUseCase;
    private final SuspendWarehouseUseCase suspendUseCase;
    private final GetWarehouseUseCase getUseCase;
    private final ListWarehouseUseCase listUseCase;

    public WarehouseController(
            CreateWarehouseUseCase createUseCase,
            UpdateWarehouseUseCase updateUseCase,
            ActivateWarehouseUseCase activateUseCase,
            SuspendWarehouseUseCase suspendUseCase,
            GetWarehouseUseCase getUseCase,
            ListWarehouseUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseIdResponse create(@Valid @RequestBody CreateWarehouseRequest request) {
        var id = createUseCase.execute(new CreateWarehouseUseCase.CreateWarehouseCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new WarehouseIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public WarehouseResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<WarehouseResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public WarehouseIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateWarehouseRequest request) {
        var updated = updateUseCase.execute(new UpdateWarehouseUseCase.UpdateWarehouseCommand(
                null, request.title(), request.description(), null, id, null));
        return new WarehouseIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateWarehouseUseCase.ActivateWarehouseCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendWarehouseRequest request) {
        suspendUseCase.execute(new SuspendWarehouseUseCase.SuspendWarehouseCommand(
                null, null, null, null, id, request.reason()));
    }

    private WarehouseResponse toResponse(Warehouse aggregate) {
        return new WarehouseResponse(
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

    public record CreateWarehouseRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateWarehouseRequest(String title, String description) {}

    public record SuspendWarehouseRequest(@NotBlank String reason) {}

    public record WarehouseIdResponse(String id) {}

    public record WarehouseResponse(
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
