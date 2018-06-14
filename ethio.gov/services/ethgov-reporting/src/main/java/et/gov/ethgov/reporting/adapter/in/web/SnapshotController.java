package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.snapshot.ActivateSnapshotUseCase;
import et.gov.ethgov.reporting.application.snapshot.CreateSnapshotUseCase;
import et.gov.ethgov.reporting.application.snapshot.GetSnapshotUseCase;
import et.gov.ethgov.reporting.application.snapshot.ListSnapshotUseCase;
import et.gov.ethgov.reporting.application.snapshot.SuspendSnapshotUseCase;
import et.gov.ethgov.reporting.application.snapshot.UpdateSnapshotUseCase;
import et.gov.ethgov.reporting.domain.snapshot.Snapshot;
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
@RequestMapping("/v1/reporting/snapshot")
public class SnapshotController {
    private final CreateSnapshotUseCase createUseCase;
    private final UpdateSnapshotUseCase updateUseCase;
    private final ActivateSnapshotUseCase activateUseCase;
    private final SuspendSnapshotUseCase suspendUseCase;
    private final GetSnapshotUseCase getUseCase;
    private final ListSnapshotUseCase listUseCase;

    public SnapshotController(
            CreateSnapshotUseCase createUseCase,
            UpdateSnapshotUseCase updateUseCase,
            ActivateSnapshotUseCase activateUseCase,
            SuspendSnapshotUseCase suspendUseCase,
            GetSnapshotUseCase getUseCase,
            ListSnapshotUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SnapshotIdResponse create(@Valid @RequestBody CreateSnapshotRequest request) {
        var id = createUseCase.execute(new CreateSnapshotUseCase.CreateSnapshotCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SnapshotIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SnapshotResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SnapshotResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SnapshotIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSnapshotRequest request) {
        var updated = updateUseCase.execute(new UpdateSnapshotUseCase.UpdateSnapshotCommand(
                null, request.title(), request.description(), null, id, null));
        return new SnapshotIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSnapshotUseCase.ActivateSnapshotCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSnapshotRequest request) {
        suspendUseCase.execute(new SuspendSnapshotUseCase.SuspendSnapshotCommand(
                null, null, null, null, id, request.reason()));
    }

    private SnapshotResponse toResponse(Snapshot aggregate) {
        return new SnapshotResponse(
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

    public record CreateSnapshotRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSnapshotRequest(String title, String description) {}

    public record SuspendSnapshotRequest(@NotBlank String reason) {}

    public record SnapshotIdResponse(String id) {}

    public record SnapshotResponse(
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
