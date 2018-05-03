package et.gov.ethgov.payments.adapter.in.web;

import et.gov.ethgov.payments.application.allocation.ActivateAllocationUseCase;
import et.gov.ethgov.payments.application.allocation.CreateAllocationUseCase;
import et.gov.ethgov.payments.application.allocation.GetAllocationUseCase;
import et.gov.ethgov.payments.application.allocation.ListAllocationUseCase;
import et.gov.ethgov.payments.application.allocation.SuspendAllocationUseCase;
import et.gov.ethgov.payments.application.allocation.UpdateAllocationUseCase;
import et.gov.ethgov.payments.domain.allocation.Allocation;
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
@RequestMapping("/v1/payments/allocation")
public class AllocationController {
    private final CreateAllocationUseCase createUseCase;
    private final UpdateAllocationUseCase updateUseCase;
    private final ActivateAllocationUseCase activateUseCase;
    private final SuspendAllocationUseCase suspendUseCase;
    private final GetAllocationUseCase getUseCase;
    private final ListAllocationUseCase listUseCase;

    public AllocationController(
            CreateAllocationUseCase createUseCase,
            UpdateAllocationUseCase updateUseCase,
            ActivateAllocationUseCase activateUseCase,
            SuspendAllocationUseCase suspendUseCase,
            GetAllocationUseCase getUseCase,
            ListAllocationUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AllocationIdResponse create(@Valid @RequestBody CreateAllocationRequest request) {
        var id = createUseCase.execute(new CreateAllocationUseCase.CreateAllocationCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new AllocationIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public AllocationResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<AllocationResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public AllocationIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateAllocationRequest request) {
        var updated = updateUseCase.execute(new UpdateAllocationUseCase.UpdateAllocationCommand(
                null, request.title(), request.description(), null, id, null));
        return new AllocationIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateAllocationUseCase.ActivateAllocationCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendAllocationRequest request) {
        suspendUseCase.execute(new SuspendAllocationUseCase.SuspendAllocationCommand(
                null, null, null, null, id, request.reason()));
    }

    private AllocationResponse toResponse(Allocation aggregate) {
        return new AllocationResponse(
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

    public record CreateAllocationRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateAllocationRequest(String title, String description) {}

    public record SuspendAllocationRequest(@NotBlank String reason) {}

    public record AllocationIdResponse(String id) {}

    public record AllocationResponse(
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
