package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.caseassignment.ActivateCaseassignmentUseCase;
import et.gov.ethgov.case.application.caseassignment.CreateCaseassignmentUseCase;
import et.gov.ethgov.case.application.caseassignment.GetCaseassignmentUseCase;
import et.gov.ethgov.case.application.caseassignment.ListCaseassignmentUseCase;
import et.gov.ethgov.case.application.caseassignment.SuspendCaseassignmentUseCase;
import et.gov.ethgov.case.application.caseassignment.UpdateCaseassignmentUseCase;
import et.gov.ethgov.case.domain.caseassignment.Caseassignment;
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
@RequestMapping("/v1/case/caseassignment")
public class CaseassignmentController {
    private final CreateCaseassignmentUseCase createUseCase;
    private final UpdateCaseassignmentUseCase updateUseCase;
    private final ActivateCaseassignmentUseCase activateUseCase;
    private final SuspendCaseassignmentUseCase suspendUseCase;
    private final GetCaseassignmentUseCase getUseCase;
    private final ListCaseassignmentUseCase listUseCase;

    public CaseassignmentController(
            CreateCaseassignmentUseCase createUseCase,
            UpdateCaseassignmentUseCase updateUseCase,
            ActivateCaseassignmentUseCase activateUseCase,
            SuspendCaseassignmentUseCase suspendUseCase,
            GetCaseassignmentUseCase getUseCase,
            ListCaseassignmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseassignmentIdResponse create(@Valid @RequestBody CreateCaseassignmentRequest request) {
        var id = createUseCase.execute(new CreateCaseassignmentUseCase.CreateCaseassignmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CaseassignmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CaseassignmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CaseassignmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CaseassignmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCaseassignmentRequest request) {
        var updated = updateUseCase.execute(new UpdateCaseassignmentUseCase.UpdateCaseassignmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new CaseassignmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCaseassignmentUseCase.ActivateCaseassignmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCaseassignmentRequest request) {
        suspendUseCase.execute(new SuspendCaseassignmentUseCase.SuspendCaseassignmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private CaseassignmentResponse toResponse(Caseassignment aggregate) {
        return new CaseassignmentResponse(
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

    public record CreateCaseassignmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCaseassignmentRequest(String title, String description) {}

    public record SuspendCaseassignmentRequest(@NotBlank String reason) {}

    public record CaseassignmentIdResponse(String id) {}

    public record CaseassignmentResponse(
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
