package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.easement.ActivateEasementUseCase;
import et.gov.ethgov.land.application.easement.CreateEasementUseCase;
import et.gov.ethgov.land.application.easement.GetEasementUseCase;
import et.gov.ethgov.land.application.easement.ListEasementUseCase;
import et.gov.ethgov.land.application.easement.SuspendEasementUseCase;
import et.gov.ethgov.land.application.easement.UpdateEasementUseCase;
import et.gov.ethgov.land.domain.easement.Easement;
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
@RequestMapping("/v1/land/easement")
public class EasementController {
    private final CreateEasementUseCase createUseCase;
    private final UpdateEasementUseCase updateUseCase;
    private final ActivateEasementUseCase activateUseCase;
    private final SuspendEasementUseCase suspendUseCase;
    private final GetEasementUseCase getUseCase;
    private final ListEasementUseCase listUseCase;

    public EasementController(
            CreateEasementUseCase createUseCase,
            UpdateEasementUseCase updateUseCase,
            ActivateEasementUseCase activateUseCase,
            SuspendEasementUseCase suspendUseCase,
            GetEasementUseCase getUseCase,
            ListEasementUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EasementIdResponse create(@Valid @RequestBody CreateEasementRequest request) {
        var id = createUseCase.execute(new CreateEasementUseCase.CreateEasementCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EasementIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EasementResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EasementResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EasementIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEasementRequest request) {
        var updated = updateUseCase.execute(new UpdateEasementUseCase.UpdateEasementCommand(
                null, request.title(), request.description(), null, id, null));
        return new EasementIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEasementUseCase.ActivateEasementCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEasementRequest request) {
        suspendUseCase.execute(new SuspendEasementUseCase.SuspendEasementCommand(
                null, null, null, null, id, request.reason()));
    }

    private EasementResponse toResponse(Easement aggregate) {
        return new EasementResponse(
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

    public record CreateEasementRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEasementRequest(String title, String description) {}

    public record SuspendEasementRequest(@NotBlank String reason) {}

    public record EasementIdResponse(String id) {}

    public record EasementResponse(
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
