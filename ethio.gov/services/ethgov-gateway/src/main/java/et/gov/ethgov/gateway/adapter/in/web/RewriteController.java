package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.rewrite.ActivateRewriteUseCase;
import et.gov.ethgov.gateway.application.rewrite.CreateRewriteUseCase;
import et.gov.ethgov.gateway.application.rewrite.GetRewriteUseCase;
import et.gov.ethgov.gateway.application.rewrite.ListRewriteUseCase;
import et.gov.ethgov.gateway.application.rewrite.SuspendRewriteUseCase;
import et.gov.ethgov.gateway.application.rewrite.UpdateRewriteUseCase;
import et.gov.ethgov.gateway.domain.rewrite.Rewrite;
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
@RequestMapping("/v1/gateway/rewrite")
public class RewriteController {
    private final CreateRewriteUseCase createUseCase;
    private final UpdateRewriteUseCase updateUseCase;
    private final ActivateRewriteUseCase activateUseCase;
    private final SuspendRewriteUseCase suspendUseCase;
    private final GetRewriteUseCase getUseCase;
    private final ListRewriteUseCase listUseCase;

    public RewriteController(
            CreateRewriteUseCase createUseCase,
            UpdateRewriteUseCase updateUseCase,
            ActivateRewriteUseCase activateUseCase,
            SuspendRewriteUseCase suspendUseCase,
            GetRewriteUseCase getUseCase,
            ListRewriteUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RewriteIdResponse create(@Valid @RequestBody CreateRewriteRequest request) {
        var id = createUseCase.execute(new CreateRewriteUseCase.CreateRewriteCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RewriteIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RewriteResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RewriteResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RewriteIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRewriteRequest request) {
        var updated = updateUseCase.execute(new UpdateRewriteUseCase.UpdateRewriteCommand(
                null, request.title(), request.description(), null, id, null));
        return new RewriteIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRewriteUseCase.ActivateRewriteCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRewriteRequest request) {
        suspendUseCase.execute(new SuspendRewriteUseCase.SuspendRewriteCommand(
                null, null, null, null, id, request.reason()));
    }

    private RewriteResponse toResponse(Rewrite aggregate) {
        return new RewriteResponse(
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

    public record CreateRewriteRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRewriteRequest(String title, String description) {}

    public record SuspendRewriteRequest(@NotBlank String reason) {}

    public record RewriteIdResponse(String id) {}

    public record RewriteResponse(
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
