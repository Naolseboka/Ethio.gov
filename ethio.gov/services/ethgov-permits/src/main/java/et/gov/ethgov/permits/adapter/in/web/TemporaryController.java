package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.temporary.ActivateTemporaryUseCase;
import et.gov.ethgov.permits.application.temporary.CreateTemporaryUseCase;
import et.gov.ethgov.permits.application.temporary.GetTemporaryUseCase;
import et.gov.ethgov.permits.application.temporary.ListTemporaryUseCase;
import et.gov.ethgov.permits.application.temporary.SuspendTemporaryUseCase;
import et.gov.ethgov.permits.application.temporary.UpdateTemporaryUseCase;
import et.gov.ethgov.permits.domain.temporary.Temporary;
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
@RequestMapping("/v1/permits/temporary")
public class TemporaryController {
    private final CreateTemporaryUseCase createUseCase;
    private final UpdateTemporaryUseCase updateUseCase;
    private final ActivateTemporaryUseCase activateUseCase;
    private final SuspendTemporaryUseCase suspendUseCase;
    private final GetTemporaryUseCase getUseCase;
    private final ListTemporaryUseCase listUseCase;

    public TemporaryController(
            CreateTemporaryUseCase createUseCase,
            UpdateTemporaryUseCase updateUseCase,
            ActivateTemporaryUseCase activateUseCase,
            SuspendTemporaryUseCase suspendUseCase,
            GetTemporaryUseCase getUseCase,
            ListTemporaryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TemporaryIdResponse create(@Valid @RequestBody CreateTemporaryRequest request) {
        var id = createUseCase.execute(new CreateTemporaryUseCase.CreateTemporaryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TemporaryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TemporaryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TemporaryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TemporaryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTemporaryRequest request) {
        var updated = updateUseCase.execute(new UpdateTemporaryUseCase.UpdateTemporaryCommand(
                null, request.title(), request.description(), null, id, null));
        return new TemporaryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTemporaryUseCase.ActivateTemporaryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTemporaryRequest request) {
        suspendUseCase.execute(new SuspendTemporaryUseCase.SuspendTemporaryCommand(
                null, null, null, null, id, request.reason()));
    }

    private TemporaryResponse toResponse(Temporary aggregate) {
        return new TemporaryResponse(
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

    public record CreateTemporaryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTemporaryRequest(String title, String description) {}

    public record SuspendTemporaryRequest(@NotBlank String reason) {}

    public record TemporaryIdResponse(String id) {}

    public record TemporaryResponse(
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
