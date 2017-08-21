package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.metric.CancelMetricDefinitionUseCase;
import et.gov.ethgov.analytics.application.metric.GetMetricDefinitionUseCase;
import et.gov.ethgov.analytics.application.metric.PublishMetricDefinitionUseCase;
import et.gov.ethgov.analytics.application.metric.RegisterMetricDefinitionUseCase;
import et.gov.ethgov.analytics.application.metric.RetireMetricDefinitionUseCase;
import et.gov.ethgov.analytics.application.metric.UpdateMetricDefinitionUseCase;
import et.gov.ethgov.analytics.domain.metric.MetricDefinition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/metrics")
public class MetricDefinitionController {

    private final RegisterMetricDefinitionUseCase registerMetric;
    private final UpdateMetricDefinitionUseCase updateMetric;
    private final PublishMetricDefinitionUseCase publishMetric;
    private final RetireMetricDefinitionUseCase retireMetric;
    private final CancelMetricDefinitionUseCase cancelMetric;
    private final GetMetricDefinitionUseCase getMetric;

    public MetricDefinitionController(
            RegisterMetricDefinitionUseCase registerMetric,
            UpdateMetricDefinitionUseCase updateMetric,
            PublishMetricDefinitionUseCase publishMetric,
            RetireMetricDefinitionUseCase retireMetric,
            CancelMetricDefinitionUseCase cancelMetric,
            GetMetricDefinitionUseCase getMetric) {
        this.registerMetric = registerMetric;
        this.updateMetric = updateMetric;
        this.publishMetric = publishMetric;
        this.retireMetric = retireMetric;
        this.cancelMetric = cancelMetric;
        this.getMetric = getMetric;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MetricIdResponse register(@Valid @RequestBody RegisterRequest request) {
        return new MetricIdResponse(registerMetric
                .execute(new RegisterMetricDefinitionUseCase.Command(
                        request.tenantId(),
                        request.code(),
                        request.name(),
                        request.description(),
                        request.category(),
                        request.unit(),
                        request.aggregation()))
                .value());
    }

    @PutMapping("/{metricDefinitionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable String metricDefinitionId, @Valid @RequestBody UpdateRequest request) {
        updateMetric.execute(new UpdateMetricDefinitionUseCase.Command(
                metricDefinitionId,
                request.name(),
                request.description(),
                request.category(),
                request.unit(),
                request.aggregation()));
    }

    @GetMapping("/lookup")
    public MetricResponse lookup(
            @RequestParam String tenantId,
            @RequestParam(required = false) String metricNumber,
            @RequestParam(required = false) String code) {
        if (code != null && !code.isBlank()) {
            return MetricResponse.from(getMetric.byCode(tenantId, code));
        }
        return MetricResponse.from(getMetric.byNumber(tenantId, metricNumber));
    }

    @GetMapping("/{metricDefinitionId}")
    public MetricResponse get(@PathVariable String metricDefinitionId) {
        return MetricResponse.from(getMetric.byId(metricDefinitionId));
    }

    @PostMapping("/{metricDefinitionId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(@PathVariable String metricDefinitionId) {
        publishMetric.execute(metricDefinitionId);
    }

    @PostMapping("/{metricDefinitionId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String metricDefinitionId) {
        retireMetric.execute(metricDefinitionId);
    }

    @PostMapping("/{metricDefinitionId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(
            @PathVariable String metricDefinitionId, @Valid @RequestBody ReasonRequest request) {
        cancelMetric.execute(metricDefinitionId, request.reason());
    }

    public record RegisterRequest(
            @NotBlank String tenantId,
            @NotBlank String code,
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            @NotBlank String category,
            @NotBlank String unit,
            @NotBlank String aggregation) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String name,
            @Size(max = 2000) String description,
            @NotBlank String category,
            @NotBlank String unit,
            @NotBlank String aggregation) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record MetricIdResponse(String metricDefinitionId) {}

    public record MetricResponse(
            String metricDefinitionId,
            String tenantId,
            String metricNumber,
            String code,
            String name,
            String description,
            String category,
            String unit,
            String aggregation,
            String status) {

        static MetricResponse from(MetricDefinition definition) {
            return new MetricResponse(
                    definition.id().value(),
                    definition.tenantId().value(),
                    definition.metricNumber().value(),
                    definition.code().value(),
                    definition.name(),
                    definition.description().orElse(null),
                    definition.category().name(),
                    definition.unit().name(),
                    definition.aggregation().name(),
                    definition.status().name());
        }
    }
}
