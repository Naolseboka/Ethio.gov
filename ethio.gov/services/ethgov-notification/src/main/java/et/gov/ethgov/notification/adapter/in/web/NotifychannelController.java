package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.notifychannel.ActivateNotifychannelUseCase;
import et.gov.ethgov.notification.application.notifychannel.CreateNotifychannelUseCase;
import et.gov.ethgov.notification.application.notifychannel.GetNotifychannelUseCase;
import et.gov.ethgov.notification.application.notifychannel.ListNotifychannelUseCase;
import et.gov.ethgov.notification.application.notifychannel.SuspendNotifychannelUseCase;
import et.gov.ethgov.notification.application.notifychannel.UpdateNotifychannelUseCase;
import et.gov.ethgov.notification.domain.notifychannel.Notifychannel;
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
@RequestMapping("/v1/notification/notifychannel")
public class NotifychannelController {
    private final CreateNotifychannelUseCase createUseCase;
    private final UpdateNotifychannelUseCase updateUseCase;
    private final ActivateNotifychannelUseCase activateUseCase;
    private final SuspendNotifychannelUseCase suspendUseCase;
    private final GetNotifychannelUseCase getUseCase;
    private final ListNotifychannelUseCase listUseCase;

    public NotifychannelController(
            CreateNotifychannelUseCase createUseCase,
            UpdateNotifychannelUseCase updateUseCase,
            ActivateNotifychannelUseCase activateUseCase,
            SuspendNotifychannelUseCase suspendUseCase,
            GetNotifychannelUseCase getUseCase,
            ListNotifychannelUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotifychannelIdResponse create(@Valid @RequestBody CreateNotifychannelRequest request) {
        var id = createUseCase.execute(new CreateNotifychannelUseCase.CreateNotifychannelCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new NotifychannelIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public NotifychannelResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<NotifychannelResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public NotifychannelIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateNotifychannelRequest request) {
        var updated = updateUseCase.execute(new UpdateNotifychannelUseCase.UpdateNotifychannelCommand(
                null, request.title(), request.description(), null, id, null));
        return new NotifychannelIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateNotifychannelUseCase.ActivateNotifychannelCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendNotifychannelRequest request) {
        suspendUseCase.execute(new SuspendNotifychannelUseCase.SuspendNotifychannelCommand(
                null, null, null, null, id, request.reason()));
    }

    private NotifychannelResponse toResponse(Notifychannel aggregate) {
        return new NotifychannelResponse(
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

    public record CreateNotifychannelRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateNotifychannelRequest(String title, String description) {}

    public record SuspendNotifychannelRequest(@NotBlank String reason) {}

    public record NotifychannelIdResponse(String id) {}

    public record NotifychannelResponse(
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
