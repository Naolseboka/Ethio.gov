package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.suspension.ActivateSuspensionUseCase;
import et.gov.ethgov.licensing.application.suspension.CreateSuspensionUseCase;
import et.gov.ethgov.licensing.application.suspension.GetSuspensionUseCase;
import et.gov.ethgov.licensing.application.suspension.ListSuspensionUseCase;
import et.gov.ethgov.licensing.application.suspension.SuspendSuspensionUseCase;
import et.gov.ethgov.licensing.application.suspension.UpdateSuspensionUseCase;
import et.gov.ethgov.licensing.domain.suspension.Suspension;
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
@RequestMapping("/v1/licensing/suspension")
public class SuspensionController {
    private final CreateSuspensionUseCase createUseCase;
    private final UpdateSuspensionUseCase updateUseCase;
    private final ActivateSuspensionUseCase activateUseCase;
    private final SuspendSuspensionUseCase suspendUseCase;
    private final GetSuspensionUseCase getUseCase;
    private final ListSuspensionUseCase listUseCase;

    public SuspensionController(
            CreateSuspensionUseCase createUseCase,
            UpdateSuspensionUseCase updateUseCase,
            ActivateSuspensionUseCase activateUseCase,
            SuspendSuspensionUseCase suspendUseCase,
            GetSuspensionUseCase getUseCase,
            ListSuspensionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuspensionIdResponse create(@Valid @RequestBody CreateSuspensionRequest request) {
        var id = createUseCase.execute(new CreateSuspensionUseCase.CreateSuspensionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SuspensionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SuspensionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SuspensionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SuspensionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSuspensionRequest request) {
        var updated = updateUseCase.execute(new UpdateSuspensionUseCase.UpdateSuspensionCommand(
                null, request.title(), request.description(), null, id, null));
        return new SuspensionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSuspensionUseCase.ActivateSuspensionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSuspensionRequest request) {
        suspendUseCase.execute(new SuspendSuspensionUseCase.SuspendSuspensionCommand(
                null, null, null, null, id, request.reason()));
    }

    private SuspensionResponse toResponse(Suspension aggregate) {
        return new SuspensionResponse(
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

    public record CreateSuspensionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSuspensionRequest(String title, String description) {}

    public record SuspendSuspensionRequest(@NotBlank String reason) {}

    public record SuspensionIdResponse(String id) {}

    public record SuspensionResponse(
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
