package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.audit.AppendAuditEntryUseCase;
import et.gov.ethgov.gateway.application.audit.GetAuditEntryUseCase;
import et.gov.ethgov.gateway.domain.audit.AuditEntry;
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
@RequestMapping("/v1/audit-entries")
public class AuditEntryController {

    private final AppendAuditEntryUseCase append;
    private final GetAuditEntryUseCase get;

    public AuditEntryController(AppendAuditEntryUseCase append, GetAuditEntryUseCase get) {
        this.append = append;
        this.get = get;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdResponse append(@Valid @RequestBody AppendRequest request) {
        return new IdResponse(append.execute(new AppendAuditEntryUseCase.Command(
                        request.tenantId(),
                        request.actorId(),
                        request.action(),
                        request.resourceType(),
                        request.resourceId(),
                        request.outcome(),
                        request.detailJson()))
                .value());
    }

    @GetMapping("/{auditEntryId}")
    public EntryResponse get(@PathVariable String auditEntryId) {
        return EntryResponse.from(get.byId(auditEntryId));
    }

    @GetMapping
    public List<EntryResponse> recent(
            @RequestParam String tenantId, @RequestParam(defaultValue = "20") int limit) {
        return get.recentByTenant(tenantId, limit).stream().map(EntryResponse::from).toList();
    }

    public record AppendRequest(
            @NotBlank String tenantId,
            @NotBlank @Size(max = 128) String actorId,
            @NotBlank @Size(max = 128) String action,
            @NotBlank @Size(max = 128) String resourceType,
            @NotBlank @Size(max = 128) String resourceId,
            @NotBlank String outcome,
            @Size(max = 4000) String detailJson) {}

    public record IdResponse(String auditEntryId) {}

    public record EntryResponse(
            String auditEntryId,
            String tenantId,
            String actorId,
            String action,
            String resourceType,
            String resourceId,
            String outcome,
            String detailJson,
            String occurredAt) {
        static EntryResponse from(AuditEntry entry) {
            return new EntryResponse(
                    entry.id().value(),
                    entry.tenantId().value(),
                    entry.actorId(),
                    entry.action(),
                    entry.resourceType(),
                    entry.resourceId(),
                    entry.outcome().name(),
                    entry.detailJson().orElse(null),
                    entry.occurredAt().toString());
        }
    }
}
