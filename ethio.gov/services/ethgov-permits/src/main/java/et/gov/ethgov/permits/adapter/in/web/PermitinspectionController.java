package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.permitinspection.ActivatePermitinspectionUseCase;
import et.gov.ethgov.permits.application.permitinspection.CreatePermitinspectionUseCase;
import et.gov.ethgov.permits.application.permitinspection.GetPermitinspectionUseCase;
import et.gov.ethgov.permits.application.permitinspection.ListPermitinspectionUseCase;
import et.gov.ethgov.permits.application.permitinspection.SuspendPermitinspectionUseCase;
import et.gov.ethgov.permits.application.permitinspection.UpdatePermitinspectionUseCase;
import et.gov.ethgov.permits.domain.permitinspection.Permitinspection;
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
@RequestMapping("/v1/permits/permitinspection")
public class PermitinspectionController {
    private final CreatePermitinspectionUseCase createUseCase;
    private final UpdatePermitinspectionUseCase updateUseCase;
    private final ActivatePermitinspectionUseCase activateUseCase;
    private final SuspendPermitinspectionUseCase suspendUseCase;
    private final GetPermitinspectionUseCase getUseCase;
    private final ListPermitinspectionUseCase listUseCase;

    public PermitinspectionController(
            CreatePermitinspectionUseCase createUseCase,
            UpdatePermitinspectionUseCase updateUseCase,
            ActivatePermitinspectionUseCase activateUseCase,
            SuspendPermitinspectionUseCase suspendUseCase,
            GetPermitinspectionUseCase getUseCase,
            ListPermitinspectionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PermitinspectionIdResponse create(@Valid @RequestBody CreatePermitinspectionRequest request) {
        var id = createUseCase.execute(new CreatePermitinspectionUseCase.CreatePermitinspectionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PermitinspectionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PermitinspectionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PermitinspectionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PermitinspectionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePermitinspectionRequest request) {
        var updated = updateUseCase.execute(new UpdatePermitinspectionUseCase.UpdatePermitinspectionCommand(
                null, request.title(), request.description(), null, id, null));
        return new PermitinspectionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePermitinspectionUseCase.ActivatePermitinspectionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPermitinspectionRequest request) {
        suspendUseCase.execute(new SuspendPermitinspectionUseCase.SuspendPermitinspectionCommand(
                null, null, null, null, id, request.reason()));
    }

    private PermitinspectionResponse toResponse(Permitinspection aggregate) {
        return new PermitinspectionResponse(
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

    public record CreatePermitinspectionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePermitinspectionRequest(String title, String description) {}

    public record SuspendPermitinspectionRequest(@NotBlank String reason) {}

    public record PermitinspectionIdResponse(String id) {}

    public record PermitinspectionResponse(
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
