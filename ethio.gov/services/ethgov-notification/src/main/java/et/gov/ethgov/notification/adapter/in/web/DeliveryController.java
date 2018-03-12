package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.delivery.ActivateDeliveryUseCase;
import et.gov.ethgov.notification.application.delivery.CreateDeliveryUseCase;
import et.gov.ethgov.notification.application.delivery.GetDeliveryUseCase;
import et.gov.ethgov.notification.application.delivery.ListDeliveryUseCase;
import et.gov.ethgov.notification.application.delivery.SuspendDeliveryUseCase;
import et.gov.ethgov.notification.application.delivery.UpdateDeliveryUseCase;
import et.gov.ethgov.notification.domain.delivery.Delivery;
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
@RequestMapping("/v1/notification/delivery")
public class DeliveryController {
    private final CreateDeliveryUseCase createUseCase;
    private final UpdateDeliveryUseCase updateUseCase;
    private final ActivateDeliveryUseCase activateUseCase;
    private final SuspendDeliveryUseCase suspendUseCase;
    private final GetDeliveryUseCase getUseCase;
    private final ListDeliveryUseCase listUseCase;

    public DeliveryController(
            CreateDeliveryUseCase createUseCase,
            UpdateDeliveryUseCase updateUseCase,
            ActivateDeliveryUseCase activateUseCase,
            SuspendDeliveryUseCase suspendUseCase,
            GetDeliveryUseCase getUseCase,
            ListDeliveryUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryIdResponse create(@Valid @RequestBody CreateDeliveryRequest request) {
        var id = createUseCase.execute(new CreateDeliveryUseCase.CreateDeliveryCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DeliveryIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DeliveryResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DeliveryResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DeliveryIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDeliveryRequest request) {
        var updated = updateUseCase.execute(new UpdateDeliveryUseCase.UpdateDeliveryCommand(
                null, request.title(), request.description(), null, id, null));
        return new DeliveryIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDeliveryUseCase.ActivateDeliveryCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDeliveryRequest request) {
        suspendUseCase.execute(new SuspendDeliveryUseCase.SuspendDeliveryCommand(
                null, null, null, null, id, request.reason()));
    }

    private DeliveryResponse toResponse(Delivery aggregate) {
        return new DeliveryResponse(
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

    public record CreateDeliveryRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDeliveryRequest(String title, String description) {}

    public record SuspendDeliveryRequest(@NotBlank String reason) {}

    public record DeliveryIdResponse(String id) {}

    public record DeliveryResponse(
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
