package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.anomaly.ActivateAnomalyUseCase;
import et.gov.ethgov.analytics.application.anomaly.CreateAnomalyUseCase;
import et.gov.ethgov.analytics.application.anomaly.GetAnomalyUseCase;
import et.gov.ethgov.analytics.application.anomaly.ListAnomalyUseCase;
import et.gov.ethgov.analytics.application.anomaly.SuspendAnomalyUseCase;
import et.gov.ethgov.analytics.application.anomaly.UpdateAnomalyUseCase;
import et.gov.ethgov.analytics.domain.anomaly.Anomaly;
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
@RequestMapping("/v1/analytics/anomaly")
public class AnomalyController {
    private final CreateAnomalyUseCase createUseCase;
    private final UpdateAnomalyUseCase updateUseCase;
    private final ActivateAnomalyUseCase activateUseCase;
    private final SuspendAnomalyUseCase suspendUseCase;
    private final GetAnomalyUseCase getUseCase;
    private final ListAnomalyUseCase listUseCase;

    public AnomalyController(
            CreateAnomalyUseCase createUseCase,
            UpdateAnomalyUseCase updateUseCase,
            ActivateAnomalyUseCase activateUseCase,
            SuspendAnomalyUseCase suspendUseCase,
            GetAnomalyUseCase getUseCase,
            ListAnomalyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnomalyIdResponse create(@Valid @RequestBody CreateAnomalyRequest request) {
        var id = createUseCase.execute(new CreateAnomalyUseCase.CreateAnomalyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AnomalyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AnomalyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AnomalyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AnomalyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAnomalyRequest request) {
        var updated = updateUseCase.execute(new UpdateAnomalyUseCase.UpdateAnomalyCommand(
                null, request.title(), request.description(), null, id, null));
        return new AnomalyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAnomalyUseCase.ActivateAnomalyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAnomalyRequest request) {
        suspendUseCase.execute(new SuspendAnomalyUseCase.SuspendAnomalyCommand(
                null, null, null, null, id, request.reason()));
    }

    private AnomalyResponse toResponse(Anomaly aggregate) {
        return new AnomalyResponse(
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

    public record CreateAnomalyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAnomalyRequest(String title, String description) {}

    public record SuspendAnomalyRequest(@NotBlank String reason) {}

    public record AnomalyIdResponse(String id) {}

    public record AnomalyResponse(
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
