package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permit.ActivatePermitUseCase;
import et.gov.ethgov.permits.application.permit.CreatePermitUseCase;
import et.gov.ethgov.permits.application.permit.GetPermitUseCase;
import et.gov.ethgov.permits.application.permit.ListPermitUseCase;
import et.gov.ethgov.permits.application.permit.SuspendPermitUseCase;
import et.gov.ethgov.permits.application.permit.UpdatePermitUseCase;
import et.gov.ethgov.permits.domain.permit.Permit;
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
@RequestMapping("/v1/permits/permit")
public class PermitController {
    private final CreatePermitUseCase createUseCase;
    private final UpdatePermitUseCase updateUseCase;
    private final ActivatePermitUseCase activateUseCase;
    private final SuspendPermitUseCase suspendUseCase;
    private final GetPermitUseCase getUseCase;
    private final ListPermitUseCase listUseCase;

    public PermitController(
            CreatePermitUseCase createUseCase,
            UpdatePermitUseCase updateUseCase,
            ActivatePermitUseCase activateUseCase,
            SuspendPermitUseCase suspendUseCase,
            GetPermitUseCase getUseCase,
            ListPermitUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitIdResponse create(@Valid @RequestBody CreatePermitRequest request) {
        var id = createUseCase.execute(new CreatePermitUseCase.CreatePermitCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PermitIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PermitResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PermitResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PermitIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePermitRequest request) {
        var updated = updateUseCase.execute(new UpdatePermitUseCase.UpdatePermitCommand(
                null, request.title(), request.description(), null, id, null));
        return new PermitIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePermitUseCase.ActivatePermitCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPermitRequest request) {
        suspendUseCase.execute(new SuspendPermitUseCase.SuspendPermitCommand(
                null, null, null, null, id, request.reason()));
    }

    private PermitResponse toResponse(Permit aggregate) {
        return new PermitResponse(
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

    public record CreatePermitRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePermitRequest(String title, String description) {}

    public record SuspendPermitRequest(@NotBlank String reason) {}

    public record PermitIdResponse(String id) {}

    public record PermitResponse(
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
