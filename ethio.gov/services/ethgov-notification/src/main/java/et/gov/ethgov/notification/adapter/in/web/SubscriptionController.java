package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.subscription.ActivateSubscriptionUseCase;
import et.gov.ethgov.notification.application.subscription.CreateSubscriptionUseCase;
import et.gov.ethgov.notification.application.subscription.GetSubscriptionUseCase;
import et.gov.ethgov.notification.application.subscription.ListSubscriptionUseCase;
import et.gov.ethgov.notification.application.subscription.SuspendSubscriptionUseCase;
import et.gov.ethgov.notification.application.subscription.UpdateSubscriptionUseCase;
import et.gov.ethgov.notification.domain.subscription.Subscription;
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
@RequestMapping("/v1/notification/subscription")
public class SubscriptionController {
    private final CreateSubscriptionUseCase createUseCase;
    private final UpdateSubscriptionUseCase updateUseCase;
    private final ActivateSubscriptionUseCase activateUseCase;
    private final SuspendSubscriptionUseCase suspendUseCase;
    private final GetSubscriptionUseCase getUseCase;
    private final ListSubscriptionUseCase listUseCase;

    public SubscriptionController(
            CreateSubscriptionUseCase createUseCase,
            UpdateSubscriptionUseCase updateUseCase,
            ActivateSubscriptionUseCase activateUseCase,
            SuspendSubscriptionUseCase suspendUseCase,
            GetSubscriptionUseCase getUseCase,
            ListSubscriptionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionIdResponse create(@Valid @RequestBody CreateSubscriptionRequest request) {
        var id = createUseCase.execute(new CreateSubscriptionUseCase.CreateSubscriptionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SubscriptionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SubscriptionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SubscriptionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SubscriptionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSubscriptionRequest request) {
        var updated = updateUseCase.execute(new UpdateSubscriptionUseCase.UpdateSubscriptionCommand(
                null, request.title(), request.description(), null, id, null));
        return new SubscriptionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSubscriptionUseCase.ActivateSubscriptionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSubscriptionRequest request) {
        suspendUseCase.execute(new SuspendSubscriptionUseCase.SuspendSubscriptionCommand(
                null, null, null, null, id, request.reason()));
    }

    private SubscriptionResponse toResponse(Subscription aggregate) {
        return new SubscriptionResponse(
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

    public record CreateSubscriptionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSubscriptionRequest(String title, String description) {}

    public record SuspendSubscriptionRequest(@NotBlank String reason) {}

    public record SubscriptionIdResponse(String id) {}

    public record SubscriptionResponse(
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
