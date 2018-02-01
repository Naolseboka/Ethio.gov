package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.boundary.ActivateBoundaryUseCase;
import et.gov.ethgov.land.application.boundary.CreateBoundaryUseCase;
import et.gov.ethgov.land.application.boundary.GetBoundaryUseCase;
import et.gov.ethgov.land.application.boundary.ListBoundaryUseCase;
import et.gov.ethgov.land.application.boundary.SuspendBoundaryUseCase;
import et.gov.ethgov.land.application.boundary.UpdateBoundaryUseCase;
import et.gov.ethgov.land.domain.boundary.Boundary;
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
@RequestMapping("/v1/land/boundary")
public class BoundaryController {
    private final CreateBoundaryUseCase createUseCase;
    private final UpdateBoundaryUseCase updateUseCase;
    private final ActivateBoundaryUseCase activateUseCase;
    private final SuspendBoundaryUseCase suspendUseCase;
    private final GetBoundaryUseCase getUseCase;
    private final ListBoundaryUseCase listUseCase;

    public BoundaryController(
            CreateBoundaryUseCase createUseCase,
            UpdateBoundaryUseCase updateUseCase,
            ActivateBoundaryUseCase activateUseCase,
            SuspendBoundaryUseCase suspendUseCase,
            GetBoundaryUseCase getUseCase,
            ListBoundaryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoundaryIdResponse create(@Valid @RequestBody CreateBoundaryRequest request) {
        var id = createUseCase.execute(new CreateBoundaryUseCase.CreateBoundaryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new BoundaryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public BoundaryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<BoundaryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public BoundaryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateBoundaryRequest request) {
        var updated = updateUseCase.execute(new UpdateBoundaryUseCase.UpdateBoundaryCommand(
                null, request.title(), request.description(), null, id, null));
        return new BoundaryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateBoundaryUseCase.ActivateBoundaryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendBoundaryRequest request) {
        suspendUseCase.execute(new SuspendBoundaryUseCase.SuspendBoundaryCommand(
                null, null, null, null, id, request.reason()));
    }

    private BoundaryResponse toResponse(Boundary aggregate) {
        return new BoundaryResponse(
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

    public record CreateBoundaryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateBoundaryRequest(String title, String description) {}

    public record SuspendBoundaryRequest(@NotBlank String reason) {}

    public record BoundaryIdResponse(String id) {}

    public record BoundaryResponse(
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
