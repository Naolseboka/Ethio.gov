package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.emergencycontact.ActivateEmergencycontactUseCase;
import et.gov.ethgov.citizen.application.emergencycontact.CreateEmergencycontactUseCase;
import et.gov.ethgov.citizen.application.emergencycontact.GetEmergencycontactUseCase;
import et.gov.ethgov.citizen.application.emergencycontact.ListEmergencycontactUseCase;
import et.gov.ethgov.citizen.application.emergencycontact.SuspendEmergencycontactUseCase;
import et.gov.ethgov.citizen.application.emergencycontact.UpdateEmergencycontactUseCase;
import et.gov.ethgov.citizen.domain.emergencycontact.Emergencycontact;
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
@RequestMapping("/v1/citizen/emergencycontact")
public class EmergencycontactController {
    private final CreateEmergencycontactUseCase createUseCase;
    private final UpdateEmergencycontactUseCase updateUseCase;
    private final ActivateEmergencycontactUseCase activateUseCase;
    private final SuspendEmergencycontactUseCase suspendUseCase;
    private final GetEmergencycontactUseCase getUseCase;
    private final ListEmergencycontactUseCase listUseCase;

    public EmergencycontactController(
            CreateEmergencycontactUseCase createUseCase,
            UpdateEmergencycontactUseCase updateUseCase,
            ActivateEmergencycontactUseCase activateUseCase,
            SuspendEmergencycontactUseCase suspendUseCase,
            GetEmergencycontactUseCase getUseCase,
            ListEmergencycontactUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmergencycontactIdResponse create(@Valid @RequestBody CreateEmergencycontactRequest request) {
        var id = createUseCase.execute(new CreateEmergencycontactUseCase.CreateEmergencycontactCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EmergencycontactIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EmergencycontactResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EmergencycontactResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EmergencycontactIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEmergencycontactRequest request) {
        var updated = updateUseCase.execute(new UpdateEmergencycontactUseCase.UpdateEmergencycontactCommand(
                null, request.title(), request.description(), null, id, null));
        return new EmergencycontactIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEmergencycontactUseCase.ActivateEmergencycontactCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEmergencycontactRequest request) {
        suspendUseCase.execute(new SuspendEmergencycontactUseCase.SuspendEmergencycontactCommand(
                null, null, null, null, id, request.reason()));
    }

    private EmergencycontactResponse toResponse(Emergencycontact aggregate) {
        return new EmergencycontactResponse(
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

    public record CreateEmergencycontactRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEmergencycontactRequest(String title, String description) {}

    public record SuspendEmergencycontactRequest(@NotBlank String reason) {}

    public record EmergencycontactIdResponse(String id) {}

    public record EmergencycontactResponse(
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
