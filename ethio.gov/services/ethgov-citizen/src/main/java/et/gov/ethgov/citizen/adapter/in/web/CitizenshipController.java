package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.citizenship.ActivateCitizenshipUseCase;
import et.gov.ethgov.citizen.application.citizenship.CreateCitizenshipUseCase;
import et.gov.ethgov.citizen.application.citizenship.GetCitizenshipUseCase;
import et.gov.ethgov.citizen.application.citizenship.ListCitizenshipUseCase;
import et.gov.ethgov.citizen.application.citizenship.SuspendCitizenshipUseCase;
import et.gov.ethgov.citizen.application.citizenship.UpdateCitizenshipUseCase;
import et.gov.ethgov.citizen.domain.citizenship.Citizenship;
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
@RequestMapping("/v1/citizen/citizenship")
public class CitizenshipController {
    private final CreateCitizenshipUseCase createUseCase;
    private final UpdateCitizenshipUseCase updateUseCase;
    private final ActivateCitizenshipUseCase activateUseCase;
    private final SuspendCitizenshipUseCase suspendUseCase;
    private final GetCitizenshipUseCase getUseCase;
    private final ListCitizenshipUseCase listUseCase;

    public CitizenshipController(
            CreateCitizenshipUseCase createUseCase,
            UpdateCitizenshipUseCase updateUseCase,
            ActivateCitizenshipUseCase activateUseCase,
            SuspendCitizenshipUseCase suspendUseCase,
            GetCitizenshipUseCase getUseCase,
            ListCitizenshipUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitizenshipIdResponse create(@Valid @RequestBody CreateCitizenshipRequest request) {
        var id = createUseCase.execute(new CreateCitizenshipUseCase.CreateCitizenshipCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CitizenshipIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CitizenshipResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CitizenshipResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CitizenshipIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCitizenshipRequest request) {
        var updated = updateUseCase.execute(new UpdateCitizenshipUseCase.UpdateCitizenshipCommand(
                null, request.title(), request.description(), null, id, null));
        return new CitizenshipIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCitizenshipUseCase.ActivateCitizenshipCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCitizenshipRequest request) {
        suspendUseCase.execute(new SuspendCitizenshipUseCase.SuspendCitizenshipCommand(
                null, null, null, null, id, request.reason()));
    }

    private CitizenshipResponse toResponse(Citizenship aggregate) {
        return new CitizenshipResponse(
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

    public record CreateCitizenshipRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCitizenshipRequest(String title, String description) {}

    public record SuspendCitizenshipRequest(@NotBlank String reason) {}

    public record CitizenshipIdResponse(String id) {}

    public record CitizenshipResponse(
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
