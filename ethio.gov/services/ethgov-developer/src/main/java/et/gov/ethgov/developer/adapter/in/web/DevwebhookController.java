package et.gov.ethgov.developer.adapter.in.web;

import et.gov.ethgov.developer.application.devwebhook.ActivateDevwebhookUseCase;
import et.gov.ethgov.developer.application.devwebhook.CreateDevwebhookUseCase;
import et.gov.ethgov.developer.application.devwebhook.GetDevwebhookUseCase;
import et.gov.ethgov.developer.application.devwebhook.ListDevwebhookUseCase;
import et.gov.ethgov.developer.application.devwebhook.SuspendDevwebhookUseCase;
import et.gov.ethgov.developer.application.devwebhook.UpdateDevwebhookUseCase;
import et.gov.ethgov.developer.domain.devwebhook.Devwebhook;
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
@RequestMapping("/v1/developer/devwebhook")
public class DevwebhookController {
    private final CreateDevwebhookUseCase createUseCase;
    private final UpdateDevwebhookUseCase updateUseCase;
    private final ActivateDevwebhookUseCase activateUseCase;
    private final SuspendDevwebhookUseCase suspendUseCase;
    private final GetDevwebhookUseCase getUseCase;
    private final ListDevwebhookUseCase listUseCase;

    public DevwebhookController(
            CreateDevwebhookUseCase createUseCase,
            UpdateDevwebhookUseCase updateUseCase,
            ActivateDevwebhookUseCase activateUseCase,
            SuspendDevwebhookUseCase suspendUseCase,
            GetDevwebhookUseCase getUseCase,
            ListDevwebhookUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DevwebhookIdResponse create(@Valid @RequestBody CreateDevwebhookRequest request) {
        var id = createUseCase.execute(new CreateDevwebhookUseCase.CreateDevwebhookCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DevwebhookIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DevwebhookResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DevwebhookResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DevwebhookIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDevwebhookRequest request) {
        var updated = updateUseCase.execute(new UpdateDevwebhookUseCase.UpdateDevwebhookCommand(
                null, request.title(), request.description(), null, id, null));
        return new DevwebhookIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDevwebhookUseCase.ActivateDevwebhookCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDevwebhookRequest request) {
        suspendUseCase.execute(new SuspendDevwebhookUseCase.SuspendDevwebhookCommand(
                null, null, null, null, id, request.reason()));
    }

    private DevwebhookResponse toResponse(Devwebhook aggregate) {
        return new DevwebhookResponse(
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

    public record CreateDevwebhookRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDevwebhookRequest(String title, String description) {}

    public record SuspendDevwebhookRequest(@NotBlank String reason) {}

    public record DevwebhookIdResponse(String id) {}

    public record DevwebhookResponse(
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
