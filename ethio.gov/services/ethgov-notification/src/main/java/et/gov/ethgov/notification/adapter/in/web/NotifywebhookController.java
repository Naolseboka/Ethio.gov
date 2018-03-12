package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.notifywebhook.ActivateNotifywebhookUseCase;
import et.gov.ethgov.notification.application.notifywebhook.CreateNotifywebhookUseCase;
import et.gov.ethgov.notification.application.notifywebhook.GetNotifywebhookUseCase;
import et.gov.ethgov.notification.application.notifywebhook.ListNotifywebhookUseCase;
import et.gov.ethgov.notification.application.notifywebhook.SuspendNotifywebhookUseCase;
import et.gov.ethgov.notification.application.notifywebhook.UpdateNotifywebhookUseCase;
import et.gov.ethgov.notification.domain.notifywebhook.Notifywebhook;
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
@RequestMapping("/v1/notification/notifywebhook")
public class NotifywebhookController {
    private final CreateNotifywebhookUseCase createUseCase;
    private final UpdateNotifywebhookUseCase updateUseCase;
    private final ActivateNotifywebhookUseCase activateUseCase;
    private final SuspendNotifywebhookUseCase suspendUseCase;
    private final GetNotifywebhookUseCase getUseCase;
    private final ListNotifywebhookUseCase listUseCase;

    public NotifywebhookController(
            CreateNotifywebhookUseCase createUseCase,
            UpdateNotifywebhookUseCase updateUseCase,
            ActivateNotifywebhookUseCase activateUseCase,
            SuspendNotifywebhookUseCase suspendUseCase,
            GetNotifywebhookUseCase getUseCase,
            ListNotifywebhookUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotifywebhookIdResponse create(@Valid @RequestBody CreateNotifywebhookRequest request) {
        var id = createUseCase.execute(new CreateNotifywebhookUseCase.CreateNotifywebhookCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new NotifywebhookIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public NotifywebhookResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<NotifywebhookResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public NotifywebhookIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateNotifywebhookRequest request) {
        var updated = updateUseCase.execute(new UpdateNotifywebhookUseCase.UpdateNotifywebhookCommand(
                null, request.title(), request.description(), null, id, null));
        return new NotifywebhookIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateNotifywebhookUseCase.ActivateNotifywebhookCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendNotifywebhookRequest request) {
        suspendUseCase.execute(new SuspendNotifywebhookUseCase.SuspendNotifywebhookCommand(
                null, null, null, null, id, request.reason()));
    }

    private NotifywebhookResponse toResponse(Notifywebhook aggregate) {
        return new NotifywebhookResponse(
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

    public record CreateNotifywebhookRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateNotifywebhookRequest(String title, String description) {}

    public record SuspendNotifywebhookRequest(@NotBlank String reason) {}

    public record NotifywebhookIdResponse(String id) {}

    public record NotifywebhookResponse(
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
