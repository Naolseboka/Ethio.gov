package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.metric.ActivateMetricUseCase;
import et.gov.ethgov.reporting.application.metric.CreateMetricUseCase;
import et.gov.ethgov.reporting.application.metric.GetMetricUseCase;
import et.gov.ethgov.reporting.application.metric.ListMetricUseCase;
import et.gov.ethgov.reporting.application.metric.SuspendMetricUseCase;
import et.gov.ethgov.reporting.application.metric.UpdateMetricUseCase;
import et.gov.ethgov.reporting.domain.metric.Metric;
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
@RequestMapping("/v1/reporting/metric")
public class MetricController {
    private final CreateMetricUseCase createUseCase;
    private final UpdateMetricUseCase updateUseCase;
    private final ActivateMetricUseCase activateUseCase;
    private final SuspendMetricUseCase suspendUseCase;
    private final GetMetricUseCase getUseCase;
    private final ListMetricUseCase listUseCase;

    public MetricController(
            CreateMetricUseCase createUseCase,
            UpdateMetricUseCase updateUseCase,
            ActivateMetricUseCase activateUseCase,
            SuspendMetricUseCase suspendUseCase,
            GetMetricUseCase getUseCase,
            ListMetricUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricIdResponse create(@Valid @RequestBody CreateMetricRequest request) {
        var id = createUseCase.execute(new CreateMetricUseCase.CreateMetricCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new MetricIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public MetricResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<MetricResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public MetricIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateMetricRequest request) {
        var updated = updateUseCase.execute(new UpdateMetricUseCase.UpdateMetricCommand(
                null, request.title(), request.description(), null, id, null));
        return new MetricIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateMetricUseCase.ActivateMetricCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendMetricRequest request) {
        suspendUseCase.execute(new SuspendMetricUseCase.SuspendMetricCommand(
                null, null, null, null, id, request.reason()));
    }

    private MetricResponse toResponse(Metric aggregate) {
        return new MetricResponse(
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

    public record CreateMetricRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateMetricRequest(String title, String description) {}

    public record SuspendMetricRequest(@NotBlank String reason) {}

    public record MetricIdResponse(String id) {}

    public record MetricResponse(
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
