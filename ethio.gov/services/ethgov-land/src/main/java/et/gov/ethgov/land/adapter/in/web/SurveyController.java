package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.survey.ActivateSurveyUseCase;
import et.gov.ethgov.land.application.survey.CreateSurveyUseCase;
import et.gov.ethgov.land.application.survey.GetSurveyUseCase;
import et.gov.ethgov.land.application.survey.ListSurveyUseCase;
import et.gov.ethgov.land.application.survey.SuspendSurveyUseCase;
import et.gov.ethgov.land.application.survey.UpdateSurveyUseCase;
import et.gov.ethgov.land.domain.survey.Survey;
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
@RequestMapping("/v1/land/survey")
public class SurveyController {
    private final CreateSurveyUseCase createUseCase;
    private final UpdateSurveyUseCase updateUseCase;
    private final ActivateSurveyUseCase activateUseCase;
    private final SuspendSurveyUseCase suspendUseCase;
    private final GetSurveyUseCase getUseCase;
    private final ListSurveyUseCase listUseCase;

    public SurveyController(
            CreateSurveyUseCase createUseCase,
            UpdateSurveyUseCase updateUseCase,
            ActivateSurveyUseCase activateUseCase,
            SuspendSurveyUseCase suspendUseCase,
            GetSurveyUseCase getUseCase,
            ListSurveyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SurveyIdResponse create(@Valid @RequestBody CreateSurveyRequest request) {
        var id = createUseCase.execute(new CreateSurveyUseCase.CreateSurveyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SurveyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SurveyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SurveyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SurveyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSurveyRequest request) {
        var updated = updateUseCase.execute(new UpdateSurveyUseCase.UpdateSurveyCommand(
                null, request.title(), request.description(), null, id, null));
        return new SurveyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSurveyUseCase.ActivateSurveyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSurveyRequest request) {
        suspendUseCase.execute(new SuspendSurveyUseCase.SuspendSurveyCommand(
                null, null, null, null, id, request.reason()));
    }

    private SurveyResponse toResponse(Survey aggregate) {
        return new SurveyResponse(
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

    public record CreateSurveyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSurveyRequest(String title, String description) {}

    public record SuspendSurveyRequest(@NotBlank String reason) {}

    public record SurveyIdResponse(String id) {}

    public record SurveyResponse(
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
