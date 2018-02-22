package et.gov.ethgov.licensing.adapter.in.web;

import et.gov.ethgov.licensing.application.endorsement.ActivateEndorsementUseCase;
import et.gov.ethgov.licensing.application.endorsement.CreateEndorsementUseCase;
import et.gov.ethgov.licensing.application.endorsement.GetEndorsementUseCase;
import et.gov.ethgov.licensing.application.endorsement.ListEndorsementUseCase;
import et.gov.ethgov.licensing.application.endorsement.SuspendEndorsementUseCase;
import et.gov.ethgov.licensing.application.endorsement.UpdateEndorsementUseCase;
import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
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
@RequestMapping("/v1/licensing/endorsement")
public class EndorsementController {
    private final CreateEndorsementUseCase createUseCase;
    private final UpdateEndorsementUseCase updateUseCase;
    private final ActivateEndorsementUseCase activateUseCase;
    private final SuspendEndorsementUseCase suspendUseCase;
    private final GetEndorsementUseCase getUseCase;
    private final ListEndorsementUseCase listUseCase;

    public EndorsementController(
            CreateEndorsementUseCase createUseCase,
            UpdateEndorsementUseCase updateUseCase,
            ActivateEndorsementUseCase activateUseCase,
            SuspendEndorsementUseCase suspendUseCase,
            GetEndorsementUseCase getUseCase,
            ListEndorsementUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EndorsementIdResponse create(@Valid @RequestBody CreateEndorsementRequest request) {
        var id = createUseCase.execute(new CreateEndorsementUseCase.CreateEndorsementCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EndorsementIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EndorsementResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EndorsementResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EndorsementIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEndorsementRequest request) {
        var updated = updateUseCase.execute(new UpdateEndorsementUseCase.UpdateEndorsementCommand(
                null, request.title(), request.description(), null, id, null));
        return new EndorsementIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEndorsementUseCase.ActivateEndorsementCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEndorsementRequest request) {
        suspendUseCase.execute(new SuspendEndorsementUseCase.SuspendEndorsementCommand(
                null, null, null, null, id, request.reason()));
    }

    private EndorsementResponse toResponse(Endorsement aggregate) {
        return new EndorsementResponse(
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

    public record CreateEndorsementRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEndorsementRequest(String title, String description) {}

    public record SuspendEndorsementRequest(@NotBlank String reason) {}

    public record EndorsementIdResponse(String id) {}

    public record EndorsementResponse(
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
