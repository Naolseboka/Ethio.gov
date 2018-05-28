package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permitcondition.ActivatePermitconditionUseCase;
import et.gov.ethgov.permits.application.permitcondition.CreatePermitconditionUseCase;
import et.gov.ethgov.permits.application.permitcondition.GetPermitconditionUseCase;
import et.gov.ethgov.permits.application.permitcondition.ListPermitconditionUseCase;
import et.gov.ethgov.permits.application.permitcondition.SuspendPermitconditionUseCase;
import et.gov.ethgov.permits.application.permitcondition.UpdatePermitconditionUseCase;
import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
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
@RequestMapping("/v1/permits/permitcondition")
public class PermitconditionController {
    private final CreatePermitconditionUseCase createUseCase;
    private final UpdatePermitconditionUseCase updateUseCase;
    private final ActivatePermitconditionUseCase activateUseCase;
    private final SuspendPermitconditionUseCase suspendUseCase;
    private final GetPermitconditionUseCase getUseCase;
    private final ListPermitconditionUseCase listUseCase;

    public PermitconditionController(
            CreatePermitconditionUseCase createUseCase,
            UpdatePermitconditionUseCase updateUseCase,
            ActivatePermitconditionUseCase activateUseCase,
            SuspendPermitconditionUseCase suspendUseCase,
            GetPermitconditionUseCase getUseCase,
            ListPermitconditionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitconditionIdResponse create(@Valid @RequestBody CreatePermitconditionRequest request) {
        var id = createUseCase.execute(new CreatePermitconditionUseCase.CreatePermitconditionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PermitconditionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PermitconditionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PermitconditionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PermitconditionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePermitconditionRequest request) {
        var updated = updateUseCase.execute(new UpdatePermitconditionUseCase.UpdatePermitconditionCommand(
                null, request.title(), request.description(), null, id, null));
        return new PermitconditionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePermitconditionUseCase.ActivatePermitconditionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPermitconditionRequest request) {
        suspendUseCase.execute(new SuspendPermitconditionUseCase.SuspendPermitconditionCommand(
                null, null, null, null, id, request.reason()));
    }

    private PermitconditionResponse toResponse(Permitcondition aggregate) {
        return new PermitconditionResponse(
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

    public record CreatePermitconditionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePermitconditionRequest(String title, String description) {}

    public record SuspendPermitconditionRequest(@NotBlank String reason) {}

    public record PermitconditionIdResponse(String id) {}

    public record PermitconditionResponse(
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
