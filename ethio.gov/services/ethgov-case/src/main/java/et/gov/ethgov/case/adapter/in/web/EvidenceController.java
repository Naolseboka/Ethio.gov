package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.evidence.ActivateEvidenceUseCase;
import et.gov.ethgov.case.application.evidence.CreateEvidenceUseCase;
import et.gov.ethgov.case.application.evidence.GetEvidenceUseCase;
import et.gov.ethgov.case.application.evidence.ListEvidenceUseCase;
import et.gov.ethgov.case.application.evidence.SuspendEvidenceUseCase;
import et.gov.ethgov.case.application.evidence.UpdateEvidenceUseCase;
import et.gov.ethgov.case.domain.evidence.Evidence;
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
@RequestMapping("/v1/case/evidence")
public class EvidenceController {
    private final CreateEvidenceUseCase createUseCase;
    private final UpdateEvidenceUseCase updateUseCase;
    private final ActivateEvidenceUseCase activateUseCase;
    private final SuspendEvidenceUseCase suspendUseCase;
    private final GetEvidenceUseCase getUseCase;
    private final ListEvidenceUseCase listUseCase;

    public EvidenceController(
            CreateEvidenceUseCase createUseCase,
            UpdateEvidenceUseCase updateUseCase,
            ActivateEvidenceUseCase activateUseCase,
            SuspendEvidenceUseCase suspendUseCase,
            GetEvidenceUseCase getUseCase,
            ListEvidenceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EvidenceIdResponse create(@Valid @RequestBody CreateEvidenceRequest request) {
        var id = createUseCase.execute(new CreateEvidenceUseCase.CreateEvidenceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EvidenceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EvidenceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EvidenceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EvidenceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEvidenceRequest request) {
        var updated = updateUseCase.execute(new UpdateEvidenceUseCase.UpdateEvidenceCommand(
                null, request.title(), request.description(), null, id, null));
        return new EvidenceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEvidenceUseCase.ActivateEvidenceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEvidenceRequest request) {
        suspendUseCase.execute(new SuspendEvidenceUseCase.SuspendEvidenceCommand(
                null, null, null, null, id, request.reason()));
    }

    private EvidenceResponse toResponse(Evidence aggregate) {
        return new EvidenceResponse(
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

    public record CreateEvidenceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEvidenceRequest(String title, String description) {}

    public record SuspendEvidenceRequest(@NotBlank String reason) {}

    public record EvidenceIdResponse(String id) {}

    public record EvidenceResponse(
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
