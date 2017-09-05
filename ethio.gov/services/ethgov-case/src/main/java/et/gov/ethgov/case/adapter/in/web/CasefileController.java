package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.casefile.ActivateCasefileUseCase;
import et.gov.ethgov.case.application.casefile.CreateCasefileUseCase;
import et.gov.ethgov.case.application.casefile.GetCasefileUseCase;
import et.gov.ethgov.case.application.casefile.ListCasefileUseCase;
import et.gov.ethgov.case.application.casefile.SuspendCasefileUseCase;
import et.gov.ethgov.case.application.casefile.UpdateCasefileUseCase;
import et.gov.ethgov.case.domain.casefile.Casefile;
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
@RequestMapping("/v1/case/casefile")
public class CasefileController {
    private final CreateCasefileUseCase createUseCase;
    private final UpdateCasefileUseCase updateUseCase;
    private final ActivateCasefileUseCase activateUseCase;
    private final SuspendCasefileUseCase suspendUseCase;
    private final GetCasefileUseCase getUseCase;
    private final ListCasefileUseCase listUseCase;

    public CasefileController(
            CreateCasefileUseCase createUseCase,
            UpdateCasefileUseCase updateUseCase,
            ActivateCasefileUseCase activateUseCase,
            SuspendCasefileUseCase suspendUseCase,
            GetCasefileUseCase getUseCase,
            ListCasefileUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CasefileIdResponse create(@Valid @RequestBody CreateCasefileRequest request) {
        var id = createUseCase.execute(new CreateCasefileUseCase.CreateCasefileCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CasefileIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CasefileResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CasefileResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CasefileIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCasefileRequest request) {
        var updated = updateUseCase.execute(new UpdateCasefileUseCase.UpdateCasefileCommand(
                null, request.title(), request.description(), null, id, null));
        return new CasefileIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCasefileUseCase.ActivateCasefileCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCasefileRequest request) {
        suspendUseCase.execute(new SuspendCasefileUseCase.SuspendCasefileCommand(
                null, null, null, null, id, request.reason()));
    }

    private CasefileResponse toResponse(Casefile aggregate) {
        return new CasefileResponse(
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

    public record CreateCasefileRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCasefileRequest(String title, String description) {}

    public record SuspendCasefileRequest(@NotBlank String reason) {}

    public record CasefileIdResponse(String id) {}

    public record CasefileResponse(
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
