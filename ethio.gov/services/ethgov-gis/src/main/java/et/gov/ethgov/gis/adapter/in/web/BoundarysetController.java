package et.gov.ethgov.gis.adapter.in.web;

import et.gov.ethgov.gis.application.boundaryset.ActivateBoundarysetUseCase;
import et.gov.ethgov.gis.application.boundaryset.CreateBoundarysetUseCase;
import et.gov.ethgov.gis.application.boundaryset.GetBoundarysetUseCase;
import et.gov.ethgov.gis.application.boundaryset.ListBoundarysetUseCase;
import et.gov.ethgov.gis.application.boundaryset.SuspendBoundarysetUseCase;
import et.gov.ethgov.gis.application.boundaryset.UpdateBoundarysetUseCase;
import et.gov.ethgov.gis.domain.boundaryset.Boundaryset;
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
@RequestMapping("/v1/gis/boundaryset")
public class BoundarysetController {
    private final CreateBoundarysetUseCase createUseCase;
    private final UpdateBoundarysetUseCase updateUseCase;
    private final ActivateBoundarysetUseCase activateUseCase;
    private final SuspendBoundarysetUseCase suspendUseCase;
    private final GetBoundarysetUseCase getUseCase;
    private final ListBoundarysetUseCase listUseCase;

    public BoundarysetController(
            CreateBoundarysetUseCase createUseCase,
            UpdateBoundarysetUseCase updateUseCase,
            ActivateBoundarysetUseCase activateUseCase,
            SuspendBoundarysetUseCase suspendUseCase,
            GetBoundarysetUseCase getUseCase,
            ListBoundarysetUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoundarysetIdResponse create(@Valid @RequestBody CreateBoundarysetRequest request) {
        var id = createUseCase.execute(new CreateBoundarysetUseCase.CreateBoundarysetCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BoundarysetIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BoundarysetResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BoundarysetResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BoundarysetIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBoundarysetRequest request) {
        var updated = updateUseCase.execute(new UpdateBoundarysetUseCase.UpdateBoundarysetCommand(
                null, request.title(), request.description(), null, id, null));
        return new BoundarysetIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBoundarysetUseCase.ActivateBoundarysetCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBoundarysetRequest request) {
        suspendUseCase.execute(new SuspendBoundarysetUseCase.SuspendBoundarysetCommand(
                null, null, null, null, id, request.reason()));
    }

    private BoundarysetResponse toResponse(Boundaryset aggregate) {
        return new BoundarysetResponse(
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

    public record CreateBoundarysetRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBoundarysetRequest(String title, String description) {}

    public record SuspendBoundarysetRequest(@NotBlank String reason) {}

    public record BoundarysetIdResponse(String id) {}

    public record BoundarysetResponse(
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
