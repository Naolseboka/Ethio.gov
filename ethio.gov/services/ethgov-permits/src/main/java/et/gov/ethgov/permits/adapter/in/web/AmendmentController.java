package et.gov.ethgov.permits.adapter.in.web;

import et.gov.ethgov.permits.application.amendment.ActivateAmendmentUseCase;
import et.gov.ethgov.permits.application.amendment.CreateAmendmentUseCase;
import et.gov.ethgov.permits.application.amendment.GetAmendmentUseCase;
import et.gov.ethgov.permits.application.amendment.ListAmendmentUseCase;
import et.gov.ethgov.permits.application.amendment.SuspendAmendmentUseCase;
import et.gov.ethgov.permits.application.amendment.UpdateAmendmentUseCase;
import et.gov.ethgov.permits.domain.amendment.Amendment;
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
@RequestMapping("/v1/permits/amendment")
public class AmendmentController {
    private final CreateAmendmentUseCase createUseCase;
    private final UpdateAmendmentUseCase updateUseCase;
    private final ActivateAmendmentUseCase activateUseCase;
    private final SuspendAmendmentUseCase suspendUseCase;
    private final GetAmendmentUseCase getUseCase;
    private final ListAmendmentUseCase listUseCase;

    public AmendmentController(
            CreateAmendmentUseCase createUseCase,
            UpdateAmendmentUseCase updateUseCase,
            ActivateAmendmentUseCase activateUseCase,
            SuspendAmendmentUseCase suspendUseCase,
            GetAmendmentUseCase getUseCase,
            ListAmendmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AmendmentIdResponse create(@Valid @RequestBody CreateAmendmentRequest request) {
        var id = createUseCase.execute(new CreateAmendmentUseCase.CreateAmendmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AmendmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AmendmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AmendmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AmendmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAmendmentRequest request) {
        var updated = updateUseCase.execute(new UpdateAmendmentUseCase.UpdateAmendmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new AmendmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAmendmentUseCase.ActivateAmendmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAmendmentRequest request) {
        suspendUseCase.execute(new SuspendAmendmentUseCase.SuspendAmendmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private AmendmentResponse toResponse(Amendment aggregate) {
        return new AmendmentResponse(
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

    public record CreateAmendmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAmendmentRequest(String title, String description) {}

    public record SuspendAmendmentRequest(@NotBlank String reason) {}

    public record AmendmentIdResponse(String id) {}

    public record AmendmentResponse(
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
