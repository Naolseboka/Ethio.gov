package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.upstream.ActivateUpstreamUseCase;
import et.gov.ethgov.gateway.application.upstream.CreateUpstreamUseCase;
import et.gov.ethgov.gateway.application.upstream.GetUpstreamUseCase;
import et.gov.ethgov.gateway.application.upstream.ListUpstreamUseCase;
import et.gov.ethgov.gateway.application.upstream.SuspendUpstreamUseCase;
import et.gov.ethgov.gateway.application.upstream.UpdateUpstreamUseCase;
import et.gov.ethgov.gateway.domain.upstream.Upstream;
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
@RequestMapping("/v1/gateway/upstream")
public class UpstreamController {
    private final CreateUpstreamUseCase createUseCase;
    private final UpdateUpstreamUseCase updateUseCase;
    private final ActivateUpstreamUseCase activateUseCase;
    private final SuspendUpstreamUseCase suspendUseCase;
    private final GetUpstreamUseCase getUseCase;
    private final ListUpstreamUseCase listUseCase;

    public UpstreamController(
            CreateUpstreamUseCase createUseCase,
            UpdateUpstreamUseCase updateUseCase,
            ActivateUpstreamUseCase activateUseCase,
            SuspendUpstreamUseCase suspendUseCase,
            GetUpstreamUseCase getUseCase,
            ListUpstreamUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UpstreamIdResponse create(@Valid @RequestBody CreateUpstreamRequest request) {
        var id = createUseCase.execute(new CreateUpstreamUseCase.CreateUpstreamCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new UpstreamIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public UpstreamResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<UpstreamResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public UpstreamIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateUpstreamRequest request) {
        var updated = updateUseCase.execute(new UpdateUpstreamUseCase.UpdateUpstreamCommand(
                null, request.title(), request.description(), null, id, null));
        return new UpstreamIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateUpstreamUseCase.ActivateUpstreamCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendUpstreamRequest request) {
        suspendUseCase.execute(new SuspendUpstreamUseCase.SuspendUpstreamCommand(
                null, null, null, null, id, request.reason()));
    }

    private UpstreamResponse toResponse(Upstream aggregate) {
        return new UpstreamResponse(
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

    public record CreateUpstreamRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateUpstreamRequest(String title, String description) {}

    public record SuspendUpstreamRequest(@NotBlank String reason) {}

    public record UpstreamIdResponse(String id) {}

    public record UpstreamResponse(
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
