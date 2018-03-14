package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.suppression.ActivateSuppressionUseCase;
import et.gov.ethgov.notification.application.suppression.CreateSuppressionUseCase;
import et.gov.ethgov.notification.application.suppression.GetSuppressionUseCase;
import et.gov.ethgov.notification.application.suppression.ListSuppressionUseCase;
import et.gov.ethgov.notification.application.suppression.SuspendSuppressionUseCase;
import et.gov.ethgov.notification.application.suppression.UpdateSuppressionUseCase;
import et.gov.ethgov.notification.domain.suppression.Suppression;
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
@RequestMapping("/v1/notification/suppression")
public class SuppressionController {
    private final CreateSuppressionUseCase createUseCase;
    private final UpdateSuppressionUseCase updateUseCase;
    private final ActivateSuppressionUseCase activateUseCase;
    private final SuspendSuppressionUseCase suspendUseCase;
    private final GetSuppressionUseCase getUseCase;
    private final ListSuppressionUseCase listUseCase;

    public SuppressionController(
            CreateSuppressionUseCase createUseCase,
            UpdateSuppressionUseCase updateUseCase,
            ActivateSuppressionUseCase activateUseCase,
            SuspendSuppressionUseCase suspendUseCase,
            GetSuppressionUseCase getUseCase,
            ListSuppressionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuppressionIdResponse create(@Valid @RequestBody CreateSuppressionRequest request) {
        var id = createUseCase.execute(new CreateSuppressionUseCase.CreateSuppressionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SuppressionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SuppressionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SuppressionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SuppressionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSuppressionRequest request) {
        var updated = updateUseCase.execute(new UpdateSuppressionUseCase.UpdateSuppressionCommand(
                null, request.title(), request.description(), null, id, null));
        return new SuppressionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSuppressionUseCase.ActivateSuppressionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSuppressionRequest request) {
        suspendUseCase.execute(new SuspendSuppressionUseCase.SuspendSuppressionCommand(
                null, null, null, null, id, request.reason()));
    }

    private SuppressionResponse toResponse(Suppression aggregate) {
        return new SuppressionResponse(
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

    public record CreateSuppressionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSuppressionRequest(String title, String description) {}

    public record SuspendSuppressionRequest(@NotBlank String reason) {}

    public record SuppressionIdResponse(String id) {}

    public record SuppressionResponse(
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
