package et.gov.ethgov.gateway.adapter.in.web;

import et.gov.ethgov.gateway.application.plugin.ActivatePluginUseCase;
import et.gov.ethgov.gateway.application.plugin.CreatePluginUseCase;
import et.gov.ethgov.gateway.application.plugin.GetPluginUseCase;
import et.gov.ethgov.gateway.application.plugin.ListPluginUseCase;
import et.gov.ethgov.gateway.application.plugin.SuspendPluginUseCase;
import et.gov.ethgov.gateway.application.plugin.UpdatePluginUseCase;
import et.gov.ethgov.gateway.domain.plugin.Plugin;
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
@RequestMapping("/v1/gateway/plugin")
public class PluginController {
    private final CreatePluginUseCase createUseCase;
    private final UpdatePluginUseCase updateUseCase;
    private final ActivatePluginUseCase activateUseCase;
    private final SuspendPluginUseCase suspendUseCase;
    private final GetPluginUseCase getUseCase;
    private final ListPluginUseCase listUseCase;

    public PluginController(
            CreatePluginUseCase createUseCase,
            UpdatePluginUseCase updateUseCase,
            ActivatePluginUseCase activateUseCase,
            SuspendPluginUseCase suspendUseCase,
            GetPluginUseCase getUseCase,
            ListPluginUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PluginIdResponse create(@Valid @RequestBody CreatePluginRequest request) {
        var id = createUseCase.execute(new CreatePluginUseCase.CreatePluginCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PluginIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PluginResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PluginResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PluginIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePluginRequest request) {
        var updated = updateUseCase.execute(new UpdatePluginUseCase.UpdatePluginCommand(
                null, request.title(), request.description(), null, id, null));
        return new PluginIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePluginUseCase.ActivatePluginCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPluginRequest request) {
        suspendUseCase.execute(new SuspendPluginUseCase.SuspendPluginCommand(
                null, null, null, null, id, request.reason()));
    }

    private PluginResponse toResponse(Plugin aggregate) {
        return new PluginResponse(
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

    public record CreatePluginRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePluginRequest(String title, String description) {}

    public record SuspendPluginRequest(@NotBlank String reason) {}

    public record PluginIdResponse(String id) {}

    public record PluginResponse(
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
