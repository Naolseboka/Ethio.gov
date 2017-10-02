package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.vitalevent.ActivateVitaleventUseCase;
import et.gov.ethgov.citizen.application.vitalevent.CreateVitaleventUseCase;
import et.gov.ethgov.citizen.application.vitalevent.GetVitaleventUseCase;
import et.gov.ethgov.citizen.application.vitalevent.ListVitaleventUseCase;
import et.gov.ethgov.citizen.application.vitalevent.SuspendVitaleventUseCase;
import et.gov.ethgov.citizen.application.vitalevent.UpdateVitaleventUseCase;
import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
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
@RequestMapping("/v1/citizen/vitalevent")
public class VitaleventController {
    private final CreateVitaleventUseCase createUseCase;
    private final UpdateVitaleventUseCase updateUseCase;
    private final ActivateVitaleventUseCase activateUseCase;
    private final SuspendVitaleventUseCase suspendUseCase;
    private final GetVitaleventUseCase getUseCase;
    private final ListVitaleventUseCase listUseCase;

    public VitaleventController(
            CreateVitaleventUseCase createUseCase,
            UpdateVitaleventUseCase updateUseCase,
            ActivateVitaleventUseCase activateUseCase,
            SuspendVitaleventUseCase suspendUseCase,
            GetVitaleventUseCase getUseCase,
            ListVitaleventUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VitaleventIdResponse create(@Valid @RequestBody CreateVitaleventRequest request) {
        var id = createUseCase.execute(new CreateVitaleventUseCase.CreateVitaleventCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new VitaleventIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public VitaleventResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<VitaleventResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public VitaleventIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateVitaleventRequest request) {
        var updated = updateUseCase.execute(new UpdateVitaleventUseCase.UpdateVitaleventCommand(
                null, request.title(), request.description(), null, id, null));
        return new VitaleventIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateVitaleventUseCase.ActivateVitaleventCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendVitaleventRequest request) {
        suspendUseCase.execute(new SuspendVitaleventUseCase.SuspendVitaleventCommand(
                null, null, null, null, id, request.reason()));
    }

    private VitaleventResponse toResponse(Vitalevent aggregate) {
        return new VitaleventResponse(
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

    public record CreateVitaleventRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateVitaleventRequest(String title, String description) {}

    public record SuspendVitaleventRequest(@NotBlank String reason) {}

    public record VitaleventIdResponse(String id) {}

    public record VitaleventResponse(
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
