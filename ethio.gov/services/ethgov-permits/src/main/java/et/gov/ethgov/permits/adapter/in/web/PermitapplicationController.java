package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permitapplication.ActivatePermitapplicationUseCase;
import et.gov.ethgov.permits.application.permitapplication.CreatePermitapplicationUseCase;
import et.gov.ethgov.permits.application.permitapplication.GetPermitapplicationUseCase;
import et.gov.ethgov.permits.application.permitapplication.ListPermitapplicationUseCase;
import et.gov.ethgov.permits.application.permitapplication.SuspendPermitapplicationUseCase;
import et.gov.ethgov.permits.application.permitapplication.UpdatePermitapplicationUseCase;
import et.gov.ethgov.permits.domain.permitapplication.Permitapplication;
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
@RequestMapping("/v1/permits/permitapplication")
public class PermitapplicationController {
    private final CreatePermitapplicationUseCase createUseCase;
    private final UpdatePermitapplicationUseCase updateUseCase;
    private final ActivatePermitapplicationUseCase activateUseCase;
    private final SuspendPermitapplicationUseCase suspendUseCase;
    private final GetPermitapplicationUseCase getUseCase;
    private final ListPermitapplicationUseCase listUseCase;

    public PermitapplicationController(
            CreatePermitapplicationUseCase createUseCase,
            UpdatePermitapplicationUseCase updateUseCase,
            ActivatePermitapplicationUseCase activateUseCase,
            SuspendPermitapplicationUseCase suspendUseCase,
            GetPermitapplicationUseCase getUseCase,
            ListPermitapplicationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitapplicationIdResponse create(@Valid @RequestBody CreatePermitapplicationRequest request) {
        var id = createUseCase.execute(new CreatePermitapplicationUseCase.CreatePermitapplicationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PermitapplicationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PermitapplicationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PermitapplicationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PermitapplicationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePermitapplicationRequest request) {
        var updated = updateUseCase.execute(new UpdatePermitapplicationUseCase.UpdatePermitapplicationCommand(
                null, request.title(), request.description(), null, id, null));
        return new PermitapplicationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePermitapplicationUseCase.ActivatePermitapplicationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPermitapplicationRequest request) {
        suspendUseCase.execute(new SuspendPermitapplicationUseCase.SuspendPermitapplicationCommand(
                null, null, null, null, id, request.reason()));
    }

    private PermitapplicationResponse toResponse(Permitapplication aggregate) {
        return new PermitapplicationResponse(
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

    public record CreatePermitapplicationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePermitapplicationRequest(String title, String description) {}

    public record SuspendPermitapplicationRequest(@NotBlank String reason) {}

    public record PermitapplicationIdResponse(String id) {}

    public record PermitapplicationResponse(
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
