package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permitchecklist.ActivatePermitchecklistUseCase;
import et.gov.ethgov.permits.application.permitchecklist.CreatePermitchecklistUseCase;
import et.gov.ethgov.permits.application.permitchecklist.GetPermitchecklistUseCase;
import et.gov.ethgov.permits.application.permitchecklist.ListPermitchecklistUseCase;
import et.gov.ethgov.permits.application.permitchecklist.SuspendPermitchecklistUseCase;
import et.gov.ethgov.permits.application.permitchecklist.UpdatePermitchecklistUseCase;
import et.gov.ethgov.permits.domain.permitchecklist.Permitchecklist;
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
@RequestMapping("/v1/permits/permitchecklist")
public class PermitchecklistController {
    private final CreatePermitchecklistUseCase createUseCase;
    private final UpdatePermitchecklistUseCase updateUseCase;
    private final ActivatePermitchecklistUseCase activateUseCase;
    private final SuspendPermitchecklistUseCase suspendUseCase;
    private final GetPermitchecklistUseCase getUseCase;
    private final ListPermitchecklistUseCase listUseCase;

    public PermitchecklistController(
            CreatePermitchecklistUseCase createUseCase,
            UpdatePermitchecklistUseCase updateUseCase,
            ActivatePermitchecklistUseCase activateUseCase,
            SuspendPermitchecklistUseCase suspendUseCase,
            GetPermitchecklistUseCase getUseCase,
            ListPermitchecklistUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitchecklistIdResponse create(@Valid @RequestBody CreatePermitchecklistRequest request) {
        var id = createUseCase.execute(new CreatePermitchecklistUseCase.CreatePermitchecklistCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PermitchecklistIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PermitchecklistResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PermitchecklistResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PermitchecklistIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePermitchecklistRequest request) {
        var updated = updateUseCase.execute(new UpdatePermitchecklistUseCase.UpdatePermitchecklistCommand(
                null, request.title(), request.description(), null, id, null));
        return new PermitchecklistIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePermitchecklistUseCase.ActivatePermitchecklistCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPermitchecklistRequest request) {
        suspendUseCase.execute(new SuspendPermitchecklistUseCase.SuspendPermitchecklistCommand(
                null, null, null, null, id, request.reason()));
    }

    private PermitchecklistResponse toResponse(Permitchecklist aggregate) {
        return new PermitchecklistResponse(
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

    public record CreatePermitchecklistRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePermitchecklistRequest(String title, String description) {}

    public record SuspendPermitchecklistRequest(@NotBlank String reason) {}

    public record PermitchecklistIdResponse(String id) {}

    public record PermitchecklistResponse(
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
