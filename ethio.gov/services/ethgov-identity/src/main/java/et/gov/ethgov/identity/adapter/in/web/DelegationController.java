package et.gov.ethgov.identity.adapter.in.web;

import et.gov.ethgov.identity.application.delegation.ActivateDelegationUseCase;
import et.gov.ethgov.identity.application.delegation.CreateDelegationUseCase;
import et.gov.ethgov.identity.application.delegation.GetDelegationUseCase;
import et.gov.ethgov.identity.application.delegation.ListDelegationUseCase;
import et.gov.ethgov.identity.application.delegation.SuspendDelegationUseCase;
import et.gov.ethgov.identity.application.delegation.UpdateDelegationUseCase;
import et.gov.ethgov.identity.domain.delegation.Delegation;
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
@RequestMapping("/v1/identity/delegation")
public class DelegationController {
    private final CreateDelegationUseCase createUseCase;
    private final UpdateDelegationUseCase updateUseCase;
    private final ActivateDelegationUseCase activateUseCase;
    private final SuspendDelegationUseCase suspendUseCase;
    private final GetDelegationUseCase getUseCase;
    private final ListDelegationUseCase listUseCase;

    public DelegationController(
            CreateDelegationUseCase createUseCase,
            UpdateDelegationUseCase updateUseCase,
            ActivateDelegationUseCase activateUseCase,
            SuspendDelegationUseCase suspendUseCase,
            GetDelegationUseCase getUseCase,
            ListDelegationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DelegationIdResponse create(@Valid @RequestBody CreateDelegationRequest request) {
        var id = createUseCase.execute(new CreateDelegationUseCase.CreateDelegationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DelegationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DelegationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DelegationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DelegationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDelegationRequest request) {
        var updated = updateUseCase.execute(new UpdateDelegationUseCase.UpdateDelegationCommand(
                null, request.title(), request.description(), null, id, null));
        return new DelegationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDelegationUseCase.ActivateDelegationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDelegationRequest request) {
        suspendUseCase.execute(new SuspendDelegationUseCase.SuspendDelegationCommand(
                null, null, null, null, id, request.reason()));
    }

    private DelegationResponse toResponse(Delegation aggregate) {
        return new DelegationResponse(
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

    public record CreateDelegationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDelegationRequest(String title, String description) {}

    public record SuspendDelegationRequest(@NotBlank String reason) {}

    public record DelegationIdResponse(String id) {}

    public record DelegationResponse(
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
