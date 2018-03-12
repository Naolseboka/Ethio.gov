package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.emailbatch.ActivateEmailbatchUseCase;
import et.gov.ethgov.notification.application.emailbatch.CreateEmailbatchUseCase;
import et.gov.ethgov.notification.application.emailbatch.GetEmailbatchUseCase;
import et.gov.ethgov.notification.application.emailbatch.ListEmailbatchUseCase;
import et.gov.ethgov.notification.application.emailbatch.SuspendEmailbatchUseCase;
import et.gov.ethgov.notification.application.emailbatch.UpdateEmailbatchUseCase;
import et.gov.ethgov.notification.domain.emailbatch.Emailbatch;
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
@RequestMapping("/v1/notification/emailbatch")
public class EmailbatchController {
    private final CreateEmailbatchUseCase createUseCase;
    private final UpdateEmailbatchUseCase updateUseCase;
    private final ActivateEmailbatchUseCase activateUseCase;
    private final SuspendEmailbatchUseCase suspendUseCase;
    private final GetEmailbatchUseCase getUseCase;
    private final ListEmailbatchUseCase listUseCase;

    public EmailbatchController(
            CreateEmailbatchUseCase createUseCase,
            UpdateEmailbatchUseCase updateUseCase,
            ActivateEmailbatchUseCase activateUseCase,
            SuspendEmailbatchUseCase suspendUseCase,
            GetEmailbatchUseCase getUseCase,
            ListEmailbatchUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmailbatchIdResponse create(@Valid @RequestBody CreateEmailbatchRequest request) {
        var id = createUseCase.execute(new CreateEmailbatchUseCase.CreateEmailbatchCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EmailbatchIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EmailbatchResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EmailbatchResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EmailbatchIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEmailbatchRequest request) {
        var updated = updateUseCase.execute(new UpdateEmailbatchUseCase.UpdateEmailbatchCommand(
                null, request.title(), request.description(), null, id, null));
        return new EmailbatchIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEmailbatchUseCase.ActivateEmailbatchCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEmailbatchRequest request) {
        suspendUseCase.execute(new SuspendEmailbatchUseCase.SuspendEmailbatchCommand(
                null, null, null, null, id, request.reason()));
    }

    private EmailbatchResponse toResponse(Emailbatch aggregate) {
        return new EmailbatchResponse(
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

    public record CreateEmailbatchRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEmailbatchRequest(String title, String description) {}

    public record SuspendEmailbatchRequest(@NotBlank String reason) {}

    public record EmailbatchIdResponse(String id) {}

    public record EmailbatchResponse(
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
