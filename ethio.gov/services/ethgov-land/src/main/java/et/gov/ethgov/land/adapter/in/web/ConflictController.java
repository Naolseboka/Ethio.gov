package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.conflict.ActivateConflictUseCase;
import et.gov.ethgov.land.application.conflict.CreateConflictUseCase;
import et.gov.ethgov.land.application.conflict.GetConflictUseCase;
import et.gov.ethgov.land.application.conflict.ListConflictUseCase;
import et.gov.ethgov.land.application.conflict.SuspendConflictUseCase;
import et.gov.ethgov.land.application.conflict.UpdateConflictUseCase;
import et.gov.ethgov.land.domain.conflict.Conflict;
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
@RequestMapping("/v1/land/conflict")
public class ConflictController {
    private final CreateConflictUseCase createUseCase;
    private final UpdateConflictUseCase updateUseCase;
    private final ActivateConflictUseCase activateUseCase;
    private final SuspendConflictUseCase suspendUseCase;
    private final GetConflictUseCase getUseCase;
    private final ListConflictUseCase listUseCase;

    public ConflictController(
            CreateConflictUseCase createUseCase,
            UpdateConflictUseCase updateUseCase,
            ActivateConflictUseCase activateUseCase,
            SuspendConflictUseCase suspendUseCase,
            GetConflictUseCase getUseCase,
            ListConflictUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConflictIdResponse create(@Valid @RequestBody CreateConflictRequest request) {
        var id = createUseCase.execute(new CreateConflictUseCase.CreateConflictCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ConflictIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ConflictResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ConflictResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ConflictIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateConflictRequest request) {
        var updated = updateUseCase.execute(new UpdateConflictUseCase.UpdateConflictCommand(
                null, request.title(), request.description(), null, id, null));
        return new ConflictIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateConflictUseCase.ActivateConflictCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendConflictRequest request) {
        suspendUseCase.execute(new SuspendConflictUseCase.SuspendConflictCommand(
                null, null, null, null, id, request.reason()));
    }

    private ConflictResponse toResponse(Conflict aggregate) {
        return new ConflictResponse(
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

    public record CreateConflictRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateConflictRequest(String title, String description) {}

    public record SuspendConflictRequest(@NotBlank String reason) {}

    public record ConflictIdResponse(String id) {}

    public record ConflictResponse(
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
