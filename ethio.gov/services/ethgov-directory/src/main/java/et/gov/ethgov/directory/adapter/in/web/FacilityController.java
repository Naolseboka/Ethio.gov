package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.facility.ActivateFacilityUseCase;
import et.gov.ethgov.directory.application.facility.CreateFacilityUseCase;
import et.gov.ethgov.directory.application.facility.GetFacilityUseCase;
import et.gov.ethgov.directory.application.facility.ListFacilityUseCase;
import et.gov.ethgov.directory.application.facility.SuspendFacilityUseCase;
import et.gov.ethgov.directory.application.facility.UpdateFacilityUseCase;
import et.gov.ethgov.directory.domain.facility.Facility;
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
@RequestMapping("/v1/directory/facility")
public class FacilityController {
    private final CreateFacilityUseCase createUseCase;
    private final UpdateFacilityUseCase updateUseCase;
    private final ActivateFacilityUseCase activateUseCase;
    private final SuspendFacilityUseCase suspendUseCase;
    private final GetFacilityUseCase getUseCase;
    private final ListFacilityUseCase listUseCase;

    public FacilityController(
            CreateFacilityUseCase createUseCase,
            UpdateFacilityUseCase updateUseCase,
            ActivateFacilityUseCase activateUseCase,
            SuspendFacilityUseCase suspendUseCase,
            GetFacilityUseCase getUseCase,
            ListFacilityUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityIdResponse create(@Valid @RequestBody CreateFacilityRequest request) {
        var id = createUseCase.execute(new CreateFacilityUseCase.CreateFacilityCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new FacilityIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public FacilityResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<FacilityResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public FacilityIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateFacilityRequest request) {
        var updated = updateUseCase.execute(new UpdateFacilityUseCase.UpdateFacilityCommand(
                null, request.title(), request.description(), null, id, null));
        return new FacilityIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateFacilityUseCase.ActivateFacilityCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendFacilityRequest request) {
        suspendUseCase.execute(new SuspendFacilityUseCase.SuspendFacilityCommand(
                null, null, null, null, id, request.reason()));
    }

    private FacilityResponse toResponse(Facility aggregate) {
        return new FacilityResponse(
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

    public record CreateFacilityRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateFacilityRequest(String title, String description) {}

    public record SuspendFacilityRequest(@NotBlank String reason) {}

    public record FacilityIdResponse(String id) {}

    public record FacilityResponse(
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
