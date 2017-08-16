package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.attribution.ActivateAttributionUseCase;
import et.gov.ethgov.analytics.application.attribution.CreateAttributionUseCase;
import et.gov.ethgov.analytics.application.attribution.GetAttributionUseCase;
import et.gov.ethgov.analytics.application.attribution.ListAttributionUseCase;
import et.gov.ethgov.analytics.application.attribution.SuspendAttributionUseCase;
import et.gov.ethgov.analytics.application.attribution.UpdateAttributionUseCase;
import et.gov.ethgov.analytics.domain.attribution.Attribution;
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
@RequestMapping("/v1/analytics/attribution")
public class AttributionController {
    private final CreateAttributionUseCase createUseCase;
    private final UpdateAttributionUseCase updateUseCase;
    private final ActivateAttributionUseCase activateUseCase;
    private final SuspendAttributionUseCase suspendUseCase;
    private final GetAttributionUseCase getUseCase;
    private final ListAttributionUseCase listUseCase;

    public AttributionController(
            CreateAttributionUseCase createUseCase,
            UpdateAttributionUseCase updateUseCase,
            ActivateAttributionUseCase activateUseCase,
            SuspendAttributionUseCase suspendUseCase,
            GetAttributionUseCase getUseCase,
            ListAttributionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttributionIdResponse create(@Valid @RequestBody CreateAttributionRequest request) {
        var id = createUseCase.execute(new CreateAttributionUseCase.CreateAttributionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AttributionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AttributionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AttributionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AttributionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAttributionRequest request) {
        var updated = updateUseCase.execute(new UpdateAttributionUseCase.UpdateAttributionCommand(
                null, request.title(), request.description(), null, id, null));
        return new AttributionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAttributionUseCase.ActivateAttributionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAttributionRequest request) {
        suspendUseCase.execute(new SuspendAttributionUseCase.SuspendAttributionCommand(
                null, null, null, null, id, request.reason()));
    }

    private AttributionResponse toResponse(Attribution aggregate) {
        return new AttributionResponse(
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

    public record CreateAttributionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAttributionRequest(String title, String description) {}

    public record SuspendAttributionRequest(@NotBlank String reason) {}

    public record AttributionIdResponse(String id) {}

    public record AttributionResponse(
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
