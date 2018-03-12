package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.dispatch.ActivateDispatchUseCase;
import et.gov.ethgov.notification.application.dispatch.CreateDispatchUseCase;
import et.gov.ethgov.notification.application.dispatch.GetDispatchUseCase;
import et.gov.ethgov.notification.application.dispatch.ListDispatchUseCase;
import et.gov.ethgov.notification.application.dispatch.SuspendDispatchUseCase;
import et.gov.ethgov.notification.application.dispatch.UpdateDispatchUseCase;
import et.gov.ethgov.notification.domain.dispatch.Dispatch;
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
@RequestMapping("/v1/notification/dispatch")
public class DispatchController {
    private final CreateDispatchUseCase createUseCase;
    private final UpdateDispatchUseCase updateUseCase;
    private final ActivateDispatchUseCase activateUseCase;
    private final SuspendDispatchUseCase suspendUseCase;
    private final GetDispatchUseCase getUseCase;
    private final ListDispatchUseCase listUseCase;

    public DispatchController(
            CreateDispatchUseCase createUseCase,
            UpdateDispatchUseCase updateUseCase,
            ActivateDispatchUseCase activateUseCase,
            SuspendDispatchUseCase suspendUseCase,
            GetDispatchUseCase getUseCase,
            ListDispatchUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispatchIdResponse create(@Valid @RequestBody CreateDispatchRequest request) {
        var id = createUseCase.execute(new CreateDispatchUseCase.CreateDispatchCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DispatchIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DispatchResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DispatchResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DispatchIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDispatchRequest request) {
        var updated = updateUseCase.execute(new UpdateDispatchUseCase.UpdateDispatchCommand(
                null, request.title(), request.description(), null, id, null));
        return new DispatchIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDispatchUseCase.ActivateDispatchCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDispatchRequest request) {
        suspendUseCase.execute(new SuspendDispatchUseCase.SuspendDispatchCommand(
                null, null, null, null, id, request.reason()));
    }

    private DispatchResponse toResponse(Dispatch aggregate) {
        return new DispatchResponse(
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

    public record CreateDispatchRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDispatchRequest(String title, String description) {}

    public record SuspendDispatchRequest(@NotBlank String reason) {}

    public record DispatchIdResponse(String id) {}

    public record DispatchResponse(
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
