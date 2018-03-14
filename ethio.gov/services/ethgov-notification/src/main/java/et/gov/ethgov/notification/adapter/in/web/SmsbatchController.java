package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.smsbatch.ActivateSmsbatchUseCase;
import et.gov.ethgov.notification.application.smsbatch.CreateSmsbatchUseCase;
import et.gov.ethgov.notification.application.smsbatch.GetSmsbatchUseCase;
import et.gov.ethgov.notification.application.smsbatch.ListSmsbatchUseCase;
import et.gov.ethgov.notification.application.smsbatch.SuspendSmsbatchUseCase;
import et.gov.ethgov.notification.application.smsbatch.UpdateSmsbatchUseCase;
import et.gov.ethgov.notification.domain.smsbatch.Smsbatch;
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
@RequestMapping("/v1/notification/smsbatch")
public class SmsbatchController {
    private final CreateSmsbatchUseCase createUseCase;
    private final UpdateSmsbatchUseCase updateUseCase;
    private final ActivateSmsbatchUseCase activateUseCase;
    private final SuspendSmsbatchUseCase suspendUseCase;
    private final GetSmsbatchUseCase getUseCase;
    private final ListSmsbatchUseCase listUseCase;

    public SmsbatchController(
            CreateSmsbatchUseCase createUseCase,
            UpdateSmsbatchUseCase updateUseCase,
            ActivateSmsbatchUseCase activateUseCase,
            SuspendSmsbatchUseCase suspendUseCase,
            GetSmsbatchUseCase getUseCase,
            ListSmsbatchUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SmsbatchIdResponse create(@Valid @RequestBody CreateSmsbatchRequest request) {
        var id = createUseCase.execute(new CreateSmsbatchUseCase.CreateSmsbatchCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SmsbatchIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SmsbatchResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SmsbatchResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SmsbatchIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSmsbatchRequest request) {
        var updated = updateUseCase.execute(new UpdateSmsbatchUseCase.UpdateSmsbatchCommand(
                null, request.title(), request.description(), null, id, null));
        return new SmsbatchIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSmsbatchUseCase.ActivateSmsbatchCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSmsbatchRequest request) {
        suspendUseCase.execute(new SuspendSmsbatchUseCase.SuspendSmsbatchCommand(
                null, null, null, null, id, request.reason()));
    }

    private SmsbatchResponse toResponse(Smsbatch aggregate) {
        return new SmsbatchResponse(
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

    public record CreateSmsbatchRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSmsbatchRequest(String title, String description) {}

    public record SuspendSmsbatchRequest(@NotBlank String reason) {}

    public record SmsbatchIdResponse(String id) {}

    public record SmsbatchResponse(
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
