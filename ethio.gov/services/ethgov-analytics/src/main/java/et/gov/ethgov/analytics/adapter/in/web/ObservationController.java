package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.observation.GetObservationUseCase;
import et.gov.ethgov.analytics.application.observation.RecordObservationUseCase;
import et.gov.ethgov.analytics.application.observation.VoidObservationUseCase;
import et.gov.ethgov.analytics.domain.observation.MetricObservation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/observations")
public class ObservationController {

    private final RecordObservationUseCase recordObservation;
    private final VoidObservationUseCase voidObservation;
    private final GetObservationUseCase getObservation;

    public ObservationController(
            RecordObservationUseCase recordObservation,
            VoidObservationUseCase voidObservation,
            GetObservationUseCase getObservation) {
        this.recordObservation = recordObservation;
        this.voidObservation = voidObservation;
        this.getObservation = getObservation;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationIdResponse record(@Valid @RequestBody RecordRequest request) {
        return new ObservationIdResponse(recordObservation
                .execute(new RecordObservationUseCase.Command(
                        request.tenantId(),
                        request.metricDefinitionId(),
                        request.value(),
                        request.periodStart(),
                        request.periodEnd(),
                        request.dimensionsJson(),
                        request.sourceReference()))
                .value());
    }

    @PostMapping("/{observationId}/void")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void voidObservation(
            @PathVariable String observationId, @Valid @RequestBody ReasonRequest request) {
        voidObservation.execute(observationId, request.reason());
    }

    @GetMapping("/lookup")
    public ObservationResponse lookup(
            @RequestParam String tenantId, @RequestParam String observationNumber) {
        return ObservationResponse.from(getObservation.byNumber(tenantId, observationNumber));
    }

    @GetMapping("/by-metric/{metricDefinitionId}")
    public List<ObservationResponse> byMetric(@PathVariable String metricDefinitionId) {
        return getObservation.byMetric(metricDefinitionId).stream()
                .map(ObservationResponse::from)
                .toList();
    }

    @GetMapping("/{observationId}")
    public ObservationResponse get(@PathVariable String observationId) {
        return ObservationResponse.from(getObservation.byId(observationId));
    }

    public record RecordRequest(
            @NotBlank String tenantId,
            @NotBlank String metricDefinitionId,
            @NotBlank String value,
            @NotBlank String periodStart,
            @NotBlank String periodEnd,
            String dimensionsJson,
            String sourceReference) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record ObservationIdResponse(String observationId) {}

    public record ObservationResponse(
            String observationId,
            String tenantId,
            String observationNumber,
            String metricDefinitionId,
            String value,
            String periodStart,
            String periodEnd,
            String dimensionsJson,
            String sourceReference,
            String status,
            String voidReason) {

        static ObservationResponse from(MetricObservation observation) {
            return new ObservationResponse(
                    observation.id().value(),
                    observation.tenantId().value(),
                    observation.observationNumber().value(),
                    observation.metricDefinitionId().value(),
                    observation.value().stripTrailingZeros().toPlainString(),
                    observation.periodStart().toString(),
                    observation.periodEnd().toString(),
                    observation.dimensionsJson(),
                    observation.sourceReference().orElse(null),
                    observation.status().name(),
                    observation.voidReason().orElse(null));
        }
    }
}
