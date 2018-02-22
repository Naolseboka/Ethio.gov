package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.compliance.ActivateComplianceUseCase;
import et.gov.ethgov.licensing.application.compliance.CreateComplianceUseCase;
import et.gov.ethgov.licensing.application.compliance.GetComplianceUseCase;
import et.gov.ethgov.licensing.application.compliance.ListComplianceUseCase;
import et.gov.ethgov.licensing.application.compliance.SuspendComplianceUseCase;
import et.gov.ethgov.licensing.application.compliance.UpdateComplianceUseCase;
import et.gov.ethgov.licensing.domain.compliance.Compliance;
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
@RequestMapping("/v1/licensing/compliance")
public class ComplianceController {
    private final CreateComplianceUseCase createUseCase;
    private final UpdateComplianceUseCase updateUseCase;
    private final ActivateComplianceUseCase activateUseCase;
    private final SuspendComplianceUseCase suspendUseCase;
    private final GetComplianceUseCase getUseCase;
    private final ListComplianceUseCase listUseCase;

    public ComplianceController(
            CreateComplianceUseCase createUseCase,
            UpdateComplianceUseCase updateUseCase,
            ActivateComplianceUseCase activateUseCase,
            SuspendComplianceUseCase suspendUseCase,
            GetComplianceUseCase getUseCase,
            ListComplianceUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplianceIdResponse create(@Valid @RequestBody CreateComplianceRequest request) {
        var id = createUseCase.execute(new CreateComplianceUseCase.CreateComplianceCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ComplianceIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ComplianceResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ComplianceResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ComplianceIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateComplianceRequest request) {
        var updated = updateUseCase.execute(new UpdateComplianceUseCase.UpdateComplianceCommand(
                null, request.title(), request.description(), null, id, null));
        return new ComplianceIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateComplianceUseCase.ActivateComplianceCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendComplianceRequest request) {
        suspendUseCase.execute(new SuspendComplianceUseCase.SuspendComplianceCommand(
                null, null, null, null, id, request.reason()));
    }

    private ComplianceResponse toResponse(Compliance aggregate) {
        return new ComplianceResponse(
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

    public record CreateComplianceRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateComplianceRequest(String title, String description) {}

    public record SuspendComplianceRequest(@NotBlank String reason) {}

    public record ComplianceIdResponse(String id) {}

    public record ComplianceResponse(
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
