package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.encumbrance.ActivateEncumbranceUseCase;
import et.gov.ethgov.land.application.encumbrance.CreateEncumbranceUseCase;
import et.gov.ethgov.land.application.encumbrance.GetEncumbranceUseCase;
import et.gov.ethgov.land.application.encumbrance.ListEncumbranceUseCase;
import et.gov.ethgov.land.application.encumbrance.SuspendEncumbranceUseCase;
import et.gov.ethgov.land.application.encumbrance.UpdateEncumbranceUseCase;
import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
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
@RequestMapping("/v1/land/encumbrance")
public class EncumbranceController {
    private final CreateEncumbranceUseCase createUseCase;
    private final UpdateEncumbranceUseCase updateUseCase;
    private final ActivateEncumbranceUseCase activateUseCase;
    private final SuspendEncumbranceUseCase suspendUseCase;
    private final GetEncumbranceUseCase getUseCase;
    private final ListEncumbranceUseCase listUseCase;

    public EncumbranceController(
            CreateEncumbranceUseCase createUseCase,
            UpdateEncumbranceUseCase updateUseCase,
            ActivateEncumbranceUseCase activateUseCase,
            SuspendEncumbranceUseCase suspendUseCase,
            GetEncumbranceUseCase getUseCase,
            ListEncumbranceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EncumbranceIdResponse create(@Valid @RequestBody CreateEncumbranceRequest request) {
        var id = createUseCase.execute(new CreateEncumbranceUseCase.CreateEncumbranceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EncumbranceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EncumbranceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EncumbranceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EncumbranceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEncumbranceRequest request) {
        var updated = updateUseCase.execute(new UpdateEncumbranceUseCase.UpdateEncumbranceCommand(
                null, request.title(), request.description(), null, id, null));
        return new EncumbranceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEncumbranceUseCase.ActivateEncumbranceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEncumbranceRequest request) {
        suspendUseCase.execute(new SuspendEncumbranceUseCase.SuspendEncumbranceCommand(
                null, null, null, null, id, request.reason()));
    }

    private EncumbranceResponse toResponse(Encumbrance aggregate) {
        return new EncumbranceResponse(
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

    public record CreateEncumbranceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEncumbranceRequest(String title, String description) {}

    public record SuspendEncumbranceRequest(@NotBlank String reason) {}

    public record EncumbranceIdResponse(String id) {}

    public record EncumbranceResponse(
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
