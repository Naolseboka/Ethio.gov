package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.experiment.ActivateExperimentUseCase;
import et.gov.ethgov.analytics.application.experiment.CreateExperimentUseCase;
import et.gov.ethgov.analytics.application.experiment.GetExperimentUseCase;
import et.gov.ethgov.analytics.application.experiment.ListExperimentUseCase;
import et.gov.ethgov.analytics.application.experiment.SuspendExperimentUseCase;
import et.gov.ethgov.analytics.application.experiment.UpdateExperimentUseCase;
import et.gov.ethgov.analytics.domain.experiment.Experiment;
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
@RequestMapping("/v1/analytics/experiment")
public class ExperimentController {
    private final CreateExperimentUseCase createUseCase;
    private final UpdateExperimentUseCase updateUseCase;
    private final ActivateExperimentUseCase activateUseCase;
    private final SuspendExperimentUseCase suspendUseCase;
    private final GetExperimentUseCase getUseCase;
    private final ListExperimentUseCase listUseCase;

    public ExperimentController(
            CreateExperimentUseCase createUseCase,
            UpdateExperimentUseCase updateUseCase,
            ActivateExperimentUseCase activateUseCase,
            SuspendExperimentUseCase suspendUseCase,
            GetExperimentUseCase getUseCase,
            ListExperimentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentIdResponse create(@Valid @RequestBody CreateExperimentRequest request) {
        var id = createUseCase.execute(new CreateExperimentUseCase.CreateExperimentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ExperimentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ExperimentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ExperimentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ExperimentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateExperimentRequest request) {
        var updated = updateUseCase.execute(new UpdateExperimentUseCase.UpdateExperimentCommand(
                null, request.title(), request.description(), null, id, null));
        return new ExperimentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateExperimentUseCase.ActivateExperimentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendExperimentRequest request) {
        suspendUseCase.execute(new SuspendExperimentUseCase.SuspendExperimentCommand(
                null, null, null, null, id, request.reason()));
    }

    private ExperimentResponse toResponse(Experiment aggregate) {
        return new ExperimentResponse(
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

    public record CreateExperimentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateExperimentRequest(String title, String description) {}

    public record SuspendExperimentRequest(@NotBlank String reason) {}

    public record ExperimentIdResponse(String id) {}

    public record ExperimentResponse(
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
