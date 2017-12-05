package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.auditlog.ActivateAuditlogUseCase;
import et.gov.ethgov.gateway.application.auditlog.CreateAuditlogUseCase;
import et.gov.ethgov.gateway.application.auditlog.GetAuditlogUseCase;
import et.gov.ethgov.gateway.application.auditlog.ListAuditlogUseCase;
import et.gov.ethgov.gateway.application.auditlog.SuspendAuditlogUseCase;
import et.gov.ethgov.gateway.application.auditlog.UpdateAuditlogUseCase;
import et.gov.ethgov.gateway.domain.auditlog.Auditlog;
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
@RequestMapping("/v1/gateway/auditlog")
public class AuditlogController {
    private final CreateAuditlogUseCase createUseCase;
    private final UpdateAuditlogUseCase updateUseCase;
    private final ActivateAuditlogUseCase activateUseCase;
    private final SuspendAuditlogUseCase suspendUseCase;
    private final GetAuditlogUseCase getUseCase;
    private final ListAuditlogUseCase listUseCase;

    public AuditlogController(
            CreateAuditlogUseCase createUseCase,
            UpdateAuditlogUseCase updateUseCase,
            ActivateAuditlogUseCase activateUseCase,
            SuspendAuditlogUseCase suspendUseCase,
            GetAuditlogUseCase getUseCase,
            ListAuditlogUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuditlogIdResponse create(@Valid @RequestBody CreateAuditlogRequest request) {
        var id = createUseCase.execute(new CreateAuditlogUseCase.CreateAuditlogCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AuditlogIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AuditlogResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AuditlogResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AuditlogIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAuditlogRequest request) {
        var updated = updateUseCase.execute(new UpdateAuditlogUseCase.UpdateAuditlogCommand(
                null, request.title(), request.description(), null, id, null));
        return new AuditlogIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAuditlogUseCase.ActivateAuditlogCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAuditlogRequest request) {
        suspendUseCase.execute(new SuspendAuditlogUseCase.SuspendAuditlogCommand(
                null, null, null, null, id, request.reason()));
    }

    private AuditlogResponse toResponse(Auditlog aggregate) {
        return new AuditlogResponse(
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

    public record CreateAuditlogRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAuditlogRequest(String title, String description) {}

    public record SuspendAuditlogRequest(@NotBlank String reason) {}

    public record AuditlogIdResponse(String id) {}

    public record AuditlogResponse(
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
