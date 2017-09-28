package et.gov.ethgov.citizen.adapter.in.web;

import et.gov.ethgov.citizen.application.residency.ActivateResidencyUseCase;
import et.gov.ethgov.citizen.application.residency.CreateResidencyUseCase;
import et.gov.ethgov.citizen.application.residency.GetResidencyUseCase;
import et.gov.ethgov.citizen.application.residency.ListResidencyUseCase;
import et.gov.ethgov.citizen.application.residency.SuspendResidencyUseCase;
import et.gov.ethgov.citizen.application.residency.UpdateResidencyUseCase;
import et.gov.ethgov.citizen.domain.residency.Residency;
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
@RequestMapping("/v1/citizen/residency")
public class ResidencyController {
    private final CreateResidencyUseCase createUseCase;
    private final UpdateResidencyUseCase updateUseCase;
    private final ActivateResidencyUseCase activateUseCase;
    private final SuspendResidencyUseCase suspendUseCase;
    private final GetResidencyUseCase getUseCase;
    private final ListResidencyUseCase listUseCase;

    public ResidencyController(
            CreateResidencyUseCase createUseCase,
            UpdateResidencyUseCase updateUseCase,
            ActivateResidencyUseCase activateUseCase,
            SuspendResidencyUseCase suspendUseCase,
            GetResidencyUseCase getUseCase,
            ListResidencyUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResidencyIdResponse create(@Valid @RequestBody CreateResidencyRequest request) {
        var id = createUseCase.execute(new CreateResidencyUseCase.CreateResidencyCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ResidencyIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ResidencyResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ResidencyResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ResidencyIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateResidencyRequest request) {
        var updated = updateUseCase.execute(new UpdateResidencyUseCase.UpdateResidencyCommand(
                null, request.title(), request.description(), null, id, null));
        return new ResidencyIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateResidencyUseCase.ActivateResidencyCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendResidencyRequest request) {
        suspendUseCase.execute(new SuspendResidencyUseCase.SuspendResidencyCommand(
                null, null, null, null, id, request.reason()));
    }

    private ResidencyResponse toResponse(Residency aggregate) {
        return new ResidencyResponse(
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

    public record CreateResidencyRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateResidencyRequest(String title, String description) {}

    public record SuspendResidencyRequest(@NotBlank String reason) {}

    public record ResidencyIdResponse(String id) {}

    public record ResidencyResponse(
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
