package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.funnel.ActivateFunnelUseCase;
import et.gov.ethgov.analytics.application.funnel.CreateFunnelUseCase;
import et.gov.ethgov.analytics.application.funnel.GetFunnelUseCase;
import et.gov.ethgov.analytics.application.funnel.ListFunnelUseCase;
import et.gov.ethgov.analytics.application.funnel.SuspendFunnelUseCase;
import et.gov.ethgov.analytics.application.funnel.UpdateFunnelUseCase;
import et.gov.ethgov.analytics.domain.funnel.Funnel;
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
@RequestMapping("/v1/analytics/funnel")
public class FunnelController {
    private final CreateFunnelUseCase createUseCase;
    private final UpdateFunnelUseCase updateUseCase;
    private final ActivateFunnelUseCase activateUseCase;
    private final SuspendFunnelUseCase suspendUseCase;
    private final GetFunnelUseCase getUseCase;
    private final ListFunnelUseCase listUseCase;

    public FunnelController(
            CreateFunnelUseCase createUseCase,
            UpdateFunnelUseCase updateUseCase,
            ActivateFunnelUseCase activateUseCase,
            SuspendFunnelUseCase suspendUseCase,
            GetFunnelUseCase getUseCase,
            ListFunnelUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FunnelIdResponse create(@Valid @RequestBody CreateFunnelRequest request) {
        var id = createUseCase.execute(new CreateFunnelUseCase.CreateFunnelCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new FunnelIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public FunnelResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<FunnelResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public FunnelIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateFunnelRequest request) {
        var updated = updateUseCase.execute(new UpdateFunnelUseCase.UpdateFunnelCommand(
                null, request.title(), request.description(), null, id, null));
        return new FunnelIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateFunnelUseCase.ActivateFunnelCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendFunnelRequest request) {
        suspendUseCase.execute(new SuspendFunnelUseCase.SuspendFunnelCommand(
                null, null, null, null, id, request.reason()));
    }

    private FunnelResponse toResponse(Funnel aggregate) {
        return new FunnelResponse(
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

    public record CreateFunnelRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateFunnelRequest(String title, String description) {}

    public record SuspendFunnelRequest(@NotBlank String reason) {}

    public record FunnelIdResponse(String id) {}

    public record FunnelResponse(
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
