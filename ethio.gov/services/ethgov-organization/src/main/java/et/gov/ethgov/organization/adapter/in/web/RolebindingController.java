package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.rolebinding.ActivateRolebindingUseCase;
import et.gov.ethgov.organization.application.rolebinding.CreateRolebindingUseCase;
import et.gov.ethgov.organization.application.rolebinding.GetRolebindingUseCase;
import et.gov.ethgov.organization.application.rolebinding.ListRolebindingUseCase;
import et.gov.ethgov.organization.application.rolebinding.SuspendRolebindingUseCase;
import et.gov.ethgov.organization.application.rolebinding.UpdateRolebindingUseCase;
import et.gov.ethgov.organization.domain.rolebinding.Rolebinding;
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
@RequestMapping("/v1/organization/rolebinding")
public class RolebindingController {
    private final CreateRolebindingUseCase createUseCase;
    private final UpdateRolebindingUseCase updateUseCase;
    private final ActivateRolebindingUseCase activateUseCase;
    private final SuspendRolebindingUseCase suspendUseCase;
    private final GetRolebindingUseCase getUseCase;
    private final ListRolebindingUseCase listUseCase;

    public RolebindingController(
            CreateRolebindingUseCase createUseCase,
            UpdateRolebindingUseCase updateUseCase,
            ActivateRolebindingUseCase activateUseCase,
            SuspendRolebindingUseCase suspendUseCase,
            GetRolebindingUseCase getUseCase,
            ListRolebindingUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolebindingIdResponse create(@Valid @RequestBody CreateRolebindingRequest request) {
        var id = createUseCase.execute(new CreateRolebindingUseCase.CreateRolebindingCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RolebindingIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RolebindingResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RolebindingResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RolebindingIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRolebindingRequest request) {
        var updated = updateUseCase.execute(new UpdateRolebindingUseCase.UpdateRolebindingCommand(
                null, request.title(), request.description(), null, id, null));
        return new RolebindingIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRolebindingUseCase.ActivateRolebindingCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRolebindingRequest request) {
        suspendUseCase.execute(new SuspendRolebindingUseCase.SuspendRolebindingCommand(
                null, null, null, null, id, request.reason()));
    }

    private RolebindingResponse toResponse(Rolebinding aggregate) {
        return new RolebindingResponse(
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

    public record CreateRolebindingRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRolebindingRequest(String title, String description) {}

    public record SuspendRolebindingRequest(@NotBlank String reason) {}

    public record RolebindingIdResponse(String id) {}

    public record RolebindingResponse(
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
