package et.gov.ethgov.land.adapter.in.web;

import et.gov.ethgov.land.application.lease.ActivateLeaseUseCase;
import et.gov.ethgov.land.application.lease.CreateLeaseUseCase;
import et.gov.ethgov.land.application.lease.GetLeaseUseCase;
import et.gov.ethgov.land.application.lease.ListLeaseUseCase;
import et.gov.ethgov.land.application.lease.SuspendLeaseUseCase;
import et.gov.ethgov.land.application.lease.UpdateLeaseUseCase;
import et.gov.ethgov.land.domain.lease.Lease;
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
@RequestMapping("/v1/land/lease")
public class LeaseController {
    private final CreateLeaseUseCase createUseCase;
    private final UpdateLeaseUseCase updateUseCase;
    private final ActivateLeaseUseCase activateUseCase;
    private final SuspendLeaseUseCase suspendUseCase;
    private final GetLeaseUseCase getUseCase;
    private final ListLeaseUseCase listUseCase;

    public LeaseController(
            CreateLeaseUseCase createUseCase,
            UpdateLeaseUseCase updateUseCase,
            ActivateLeaseUseCase activateUseCase,
            SuspendLeaseUseCase suspendUseCase,
            GetLeaseUseCase getUseCase,
            ListLeaseUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaseIdResponse create(@Valid @RequestBody CreateLeaseRequest request) {
        var id = createUseCase.execute(new CreateLeaseUseCase.CreateLeaseCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new LeaseIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public LeaseResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<LeaseResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public LeaseIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateLeaseRequest request) {
        var updated = updateUseCase.execute(new UpdateLeaseUseCase.UpdateLeaseCommand(
                null, request.title(), request.description(), null, id, null));
        return new LeaseIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateLeaseUseCase.ActivateLeaseCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendLeaseRequest request) {
        suspendUseCase.execute(new SuspendLeaseUseCase.SuspendLeaseCommand(
                null, null, null, null, id, request.reason()));
    }

    private LeaseResponse toResponse(Lease aggregate) {
        return new LeaseResponse(
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

    public record CreateLeaseRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateLeaseRequest(String title, String description) {}

    public record SuspendLeaseRequest(@NotBlank String reason) {}

    public record LeaseIdResponse(String id) {}

    public record LeaseResponse(
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
