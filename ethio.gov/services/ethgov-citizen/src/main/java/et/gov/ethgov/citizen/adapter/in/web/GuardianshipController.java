package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.guardianship.ActivateGuardianshipUseCase;
import et.gov.ethgov.citizen.application.guardianship.CreateGuardianshipUseCase;
import et.gov.ethgov.citizen.application.guardianship.GetGuardianshipUseCase;
import et.gov.ethgov.citizen.application.guardianship.ListGuardianshipUseCase;
import et.gov.ethgov.citizen.application.guardianship.SuspendGuardianshipUseCase;
import et.gov.ethgov.citizen.application.guardianship.UpdateGuardianshipUseCase;
import et.gov.ethgov.citizen.domain.guardianship.Guardianship;
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
@RequestMapping("/v1/citizen/guardianship")
public class GuardianshipController {
    private final CreateGuardianshipUseCase createUseCase;
    private final UpdateGuardianshipUseCase updateUseCase;
    private final ActivateGuardianshipUseCase activateUseCase;
    private final SuspendGuardianshipUseCase suspendUseCase;
    private final GetGuardianshipUseCase getUseCase;
    private final ListGuardianshipUseCase listUseCase;

    public GuardianshipController(
            CreateGuardianshipUseCase createUseCase,
            UpdateGuardianshipUseCase updateUseCase,
            ActivateGuardianshipUseCase activateUseCase,
            SuspendGuardianshipUseCase suspendUseCase,
            GetGuardianshipUseCase getUseCase,
            ListGuardianshipUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuardianshipIdResponse create(@Valid @RequestBody CreateGuardianshipRequest request) {
        var id = createUseCase.execute(new CreateGuardianshipUseCase.CreateGuardianshipCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new GuardianshipIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public GuardianshipResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<GuardianshipResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public GuardianshipIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateGuardianshipRequest request) {
        var updated = updateUseCase.execute(new UpdateGuardianshipUseCase.UpdateGuardianshipCommand(
                null, request.title(), request.description(), null, id, null));
        return new GuardianshipIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateGuardianshipUseCase.ActivateGuardianshipCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendGuardianshipRequest request) {
        suspendUseCase.execute(new SuspendGuardianshipUseCase.SuspendGuardianshipCommand(
                null, null, null, null, id, request.reason()));
    }

    private GuardianshipResponse toResponse(Guardianship aggregate) {
        return new GuardianshipResponse(
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

    public record CreateGuardianshipRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateGuardianshipRequest(String title, String description) {}

    public record SuspendGuardianshipRequest(@NotBlank String reason) {}

    public record GuardianshipIdResponse(String id) {}

    public record GuardianshipResponse(
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
