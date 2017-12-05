package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.canary.ActivateCanaryUseCase;
import et.gov.ethgov.gateway.application.canary.CreateCanaryUseCase;
import et.gov.ethgov.gateway.application.canary.GetCanaryUseCase;
import et.gov.ethgov.gateway.application.canary.ListCanaryUseCase;
import et.gov.ethgov.gateway.application.canary.SuspendCanaryUseCase;
import et.gov.ethgov.gateway.application.canary.UpdateCanaryUseCase;
import et.gov.ethgov.gateway.domain.canary.Canary;
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
@RequestMapping("/v1/gateway/canary")
public class CanaryController {
    private final CreateCanaryUseCase createUseCase;
    private final UpdateCanaryUseCase updateUseCase;
    private final ActivateCanaryUseCase activateUseCase;
    private final SuspendCanaryUseCase suspendUseCase;
    private final GetCanaryUseCase getUseCase;
    private final ListCanaryUseCase listUseCase;

    public CanaryController(
            CreateCanaryUseCase createUseCase,
            UpdateCanaryUseCase updateUseCase,
            ActivateCanaryUseCase activateUseCase,
            SuspendCanaryUseCase suspendUseCase,
            GetCanaryUseCase getUseCase,
            ListCanaryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CanaryIdResponse create(@Valid @RequestBody CreateCanaryRequest request) {
        var id = createUseCase.execute(new CreateCanaryUseCase.CreateCanaryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CanaryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CanaryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CanaryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CanaryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCanaryRequest request) {
        var updated = updateUseCase.execute(new UpdateCanaryUseCase.UpdateCanaryCommand(
                null, request.title(), request.description(), null, id, null));
        return new CanaryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCanaryUseCase.ActivateCanaryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCanaryRequest request) {
        suspendUseCase.execute(new SuspendCanaryUseCase.SuspendCanaryCommand(
                null, null, null, null, id, request.reason()));
    }

    private CanaryResponse toResponse(Canary aggregate) {
        return new CanaryResponse(
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

    public record CreateCanaryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCanaryRequest(String title, String description) {}

    public record SuspendCanaryRequest(@NotBlank String reason) {}

    public record CanaryIdResponse(String id) {}

    public record CanaryResponse(
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
