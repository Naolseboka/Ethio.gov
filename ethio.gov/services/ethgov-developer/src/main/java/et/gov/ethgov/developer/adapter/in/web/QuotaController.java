package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.quota.ActivateQuotaUseCase;
import et.gov.ethgov.developer.application.quota.CreateQuotaUseCase;
import et.gov.ethgov.developer.application.quota.GetQuotaUseCase;
import et.gov.ethgov.developer.application.quota.ListQuotaUseCase;
import et.gov.ethgov.developer.application.quota.SuspendQuotaUseCase;
import et.gov.ethgov.developer.application.quota.UpdateQuotaUseCase;
import et.gov.ethgov.developer.domain.quota.Quota;
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
@RequestMapping("/v1/developer/quota")
public class QuotaController {
    private final CreateQuotaUseCase createUseCase;
    private final UpdateQuotaUseCase updateUseCase;
    private final ActivateQuotaUseCase activateUseCase;
    private final SuspendQuotaUseCase suspendUseCase;
    private final GetQuotaUseCase getUseCase;
    private final ListQuotaUseCase listUseCase;

    public QuotaController(
            CreateQuotaUseCase createUseCase,
            UpdateQuotaUseCase updateUseCase,
            ActivateQuotaUseCase activateUseCase,
            SuspendQuotaUseCase suspendUseCase,
            GetQuotaUseCase getUseCase,
            ListQuotaUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuotaIdResponse create(@Valid @RequestBody CreateQuotaRequest request) {
        var id = createUseCase.execute(new CreateQuotaUseCase.CreateQuotaCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new QuotaIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public QuotaResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<QuotaResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public QuotaIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateQuotaRequest request) {
        var updated = updateUseCase.execute(new UpdateQuotaUseCase.UpdateQuotaCommand(
                null, request.title(), request.description(), null, id, null));
        return new QuotaIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateQuotaUseCase.ActivateQuotaCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendQuotaRequest request) {
        suspendUseCase.execute(new SuspendQuotaUseCase.SuspendQuotaCommand(
                null, null, null, null, id, request.reason()));
    }

    private QuotaResponse toResponse(Quota aggregate) {
        return new QuotaResponse(
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

    public record CreateQuotaRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateQuotaRequest(String title, String description) {}

    public record SuspendQuotaRequest(@NotBlank String reason) {}

    public record QuotaIdResponse(String id) {}

    public record QuotaResponse(
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
