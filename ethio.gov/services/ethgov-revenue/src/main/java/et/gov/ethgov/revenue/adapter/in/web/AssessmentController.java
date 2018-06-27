package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.assessment.ActivateAssessmentUseCase;
import et.gov.ethgov.revenue.application.assessment.CreateAssessmentUseCase;
import et.gov.ethgov.revenue.application.assessment.GetAssessmentUseCase;
import et.gov.ethgov.revenue.application.assessment.ListAssessmentUseCase;
import et.gov.ethgov.revenue.application.assessment.SuspendAssessmentUseCase;
import et.gov.ethgov.revenue.application.assessment.UpdateAssessmentUseCase;
import et.gov.ethgov.revenue.domain.assessment.Assessment;
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
@RequestMapping("/v1/revenue/assessment")
public class AssessmentController {
    private final CreateAssessmentUseCase createUseCase;
    private final UpdateAssessmentUseCase updateUseCase;
    private final ActivateAssessmentUseCase activateUseCase;
    private final SuspendAssessmentUseCase suspendUseCase;
    private final GetAssessmentUseCase getUseCase;
    private final ListAssessmentUseCase listUseCase;

    public AssessmentController(
            CreateAssessmentUseCase createUseCase,
            UpdateAssessmentUseCase updateUseCase,
            ActivateAssessmentUseCase activateUseCase,
            SuspendAssessmentUseCase suspendUseCase,
            GetAssessmentUseCase getUseCase,
            ListAssessmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssessmentIdResponse create(@Valid @RequestBody CreateAssessmentRequest request) {
        var id = createUseCase.execute(new CreateAssessmentUseCase.CreateAssessmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AssessmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AssessmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AssessmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AssessmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAssessmentRequest request) {
        var updated = updateUseCase.execute(new UpdateAssessmentUseCase.UpdateAssessmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new AssessmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAssessmentUseCase.ActivateAssessmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAssessmentRequest request) {
        suspendUseCase.execute(new SuspendAssessmentUseCase.SuspendAssessmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private AssessmentResponse toResponse(Assessment aggregate) {
        return new AssessmentResponse(
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

    public record CreateAssessmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAssessmentRequest(String title, String description) {}

    public record SuspendAssessmentRequest(@NotBlank String reason) {}

    public record AssessmentIdResponse(String id) {}

    public record AssessmentResponse(
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
