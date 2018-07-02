package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.installment.ActivateInstallmentUseCase;
import et.gov.ethgov.revenue.application.installment.CreateInstallmentUseCase;
import et.gov.ethgov.revenue.application.installment.GetInstallmentUseCase;
import et.gov.ethgov.revenue.application.installment.ListInstallmentUseCase;
import et.gov.ethgov.revenue.application.installment.SuspendInstallmentUseCase;
import et.gov.ethgov.revenue.application.installment.UpdateInstallmentUseCase;
import et.gov.ethgov.revenue.domain.installment.Installment;
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
@RequestMapping("/v1/revenue/installment")
public class InstallmentController {
    private final CreateInstallmentUseCase createUseCase;
    private final UpdateInstallmentUseCase updateUseCase;
    private final ActivateInstallmentUseCase activateUseCase;
    private final SuspendInstallmentUseCase suspendUseCase;
    private final GetInstallmentUseCase getUseCase;
    private final ListInstallmentUseCase listUseCase;

    public InstallmentController(
            CreateInstallmentUseCase createUseCase,
            UpdateInstallmentUseCase updateUseCase,
            ActivateInstallmentUseCase activateUseCase,
            SuspendInstallmentUseCase suspendUseCase,
            GetInstallmentUseCase getUseCase,
            ListInstallmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstallmentIdResponse create(@Valid @RequestBody CreateInstallmentRequest request) {
        var id = createUseCase.execute(new CreateInstallmentUseCase.CreateInstallmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new InstallmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public InstallmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<InstallmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public InstallmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateInstallmentRequest request) {
        var updated = updateUseCase.execute(new UpdateInstallmentUseCase.UpdateInstallmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new InstallmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateInstallmentUseCase.ActivateInstallmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendInstallmentRequest request) {
        suspendUseCase.execute(new SuspendInstallmentUseCase.SuspendInstallmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private InstallmentResponse toResponse(Installment aggregate) {
        return new InstallmentResponse(
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

    public record CreateInstallmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateInstallmentRequest(String title, String description) {}

    public record SuspendInstallmentRequest(@NotBlank String reason) {}

    public record InstallmentIdResponse(String id) {}

    public record InstallmentResponse(
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
