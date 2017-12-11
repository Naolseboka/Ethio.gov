package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.healthcheck.ActivateHealthcheckUseCase;
import et.gov.ethgov.gateway.application.healthcheck.CreateHealthcheckUseCase;
import et.gov.ethgov.gateway.application.healthcheck.GetHealthcheckUseCase;
import et.gov.ethgov.gateway.application.healthcheck.ListHealthcheckUseCase;
import et.gov.ethgov.gateway.application.healthcheck.SuspendHealthcheckUseCase;
import et.gov.ethgov.gateway.application.healthcheck.UpdateHealthcheckUseCase;
import et.gov.ethgov.gateway.domain.healthcheck.Healthcheck;
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
@RequestMapping("/v1/gateway/healthcheck")
public class HealthcheckController {
    private final CreateHealthcheckUseCase createUseCase;
    private final UpdateHealthcheckUseCase updateUseCase;
    private final ActivateHealthcheckUseCase activateUseCase;
    private final SuspendHealthcheckUseCase suspendUseCase;
    private final GetHealthcheckUseCase getUseCase;
    private final ListHealthcheckUseCase listUseCase;

    public HealthcheckController(
            CreateHealthcheckUseCase createUseCase,
            UpdateHealthcheckUseCase updateUseCase,
            ActivateHealthcheckUseCase activateUseCase,
            SuspendHealthcheckUseCase suspendUseCase,
            GetHealthcheckUseCase getUseCase,
            ListHealthcheckUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HealthcheckIdResponse create(@Valid @RequestBody CreateHealthcheckRequest request) {
        var id = createUseCase.execute(new CreateHealthcheckUseCase.CreateHealthcheckCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new HealthcheckIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public HealthcheckResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<HealthcheckResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public HealthcheckIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateHealthcheckRequest request) {
        var updated = updateUseCase.execute(new UpdateHealthcheckUseCase.UpdateHealthcheckCommand(
                null, request.title(), request.description(), null, id, null));
        return new HealthcheckIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateHealthcheckUseCase.ActivateHealthcheckCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendHealthcheckRequest request) {
        suspendUseCase.execute(new SuspendHealthcheckUseCase.SuspendHealthcheckCommand(
                null, null, null, null, id, request.reason()));
    }

    private HealthcheckResponse toResponse(Healthcheck aggregate) {
        return new HealthcheckResponse(
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

    public record CreateHealthcheckRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateHealthcheckRequest(String title, String description) {}

    public record SuspendHealthcheckRequest(@NotBlank String reason) {}

    public record HealthcheckIdResponse(String id) {}

    public record HealthcheckResponse(
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
