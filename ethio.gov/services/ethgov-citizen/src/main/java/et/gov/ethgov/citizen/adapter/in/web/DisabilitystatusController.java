package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.disabilitystatus.ActivateDisabilitystatusUseCase;
import et.gov.ethgov.citizen.application.disabilitystatus.CreateDisabilitystatusUseCase;
import et.gov.ethgov.citizen.application.disabilitystatus.GetDisabilitystatusUseCase;
import et.gov.ethgov.citizen.application.disabilitystatus.ListDisabilitystatusUseCase;
import et.gov.ethgov.citizen.application.disabilitystatus.SuspendDisabilitystatusUseCase;
import et.gov.ethgov.citizen.application.disabilitystatus.UpdateDisabilitystatusUseCase;
import et.gov.ethgov.citizen.domain.disabilitystatus.Disabilitystatus;
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
@RequestMapping("/v1/citizen/disabilitystatus")
public class DisabilitystatusController {
    private final CreateDisabilitystatusUseCase createUseCase;
    private final UpdateDisabilitystatusUseCase updateUseCase;
    private final ActivateDisabilitystatusUseCase activateUseCase;
    private final SuspendDisabilitystatusUseCase suspendUseCase;
    private final GetDisabilitystatusUseCase getUseCase;
    private final ListDisabilitystatusUseCase listUseCase;

    public DisabilitystatusController(
            CreateDisabilitystatusUseCase createUseCase,
            UpdateDisabilitystatusUseCase updateUseCase,
            ActivateDisabilitystatusUseCase activateUseCase,
            SuspendDisabilitystatusUseCase suspendUseCase,
            GetDisabilitystatusUseCase getUseCase,
            ListDisabilitystatusUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DisabilitystatusIdResponse create(@Valid @RequestBody CreateDisabilitystatusRequest request) {
        var id = createUseCase.execute(new CreateDisabilitystatusUseCase.CreateDisabilitystatusCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DisabilitystatusIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DisabilitystatusResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DisabilitystatusResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DisabilitystatusIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDisabilitystatusRequest request) {
        var updated = updateUseCase.execute(new UpdateDisabilitystatusUseCase.UpdateDisabilitystatusCommand(
                null, request.title(), request.description(), null, id, null));
        return new DisabilitystatusIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDisabilitystatusUseCase.ActivateDisabilitystatusCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDisabilitystatusRequest request) {
        suspendUseCase.execute(new SuspendDisabilitystatusUseCase.SuspendDisabilitystatusCommand(
                null, null, null, null, id, request.reason()));
    }

    private DisabilitystatusResponse toResponse(Disabilitystatus aggregate) {
        return new DisabilitystatusResponse(
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

    public record CreateDisabilitystatusRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDisabilitystatusRequest(String title, String description) {}

    public record SuspendDisabilitystatusRequest(@NotBlank String reason) {}

    public record DisabilitystatusIdResponse(String id) {}

    public record DisabilitystatusResponse(
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
