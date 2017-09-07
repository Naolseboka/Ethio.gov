package et.gov.ethgov.case.adapter.in.web;

import et.gov.ethgov.case.application.priority.ActivatePriorityUseCase;
import et.gov.ethgov.case.application.priority.CreatePriorityUseCase;
import et.gov.ethgov.case.application.priority.GetPriorityUseCase;
import et.gov.ethgov.case.application.priority.ListPriorityUseCase;
import et.gov.ethgov.case.application.priority.SuspendPriorityUseCase;
import et.gov.ethgov.case.application.priority.UpdatePriorityUseCase;
import et.gov.ethgov.case.domain.priority.Priority;
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
@RequestMapping("/v1/case/priority")
public class PriorityController {
    private final CreatePriorityUseCase createUseCase;
    private final UpdatePriorityUseCase updateUseCase;
    private final ActivatePriorityUseCase activateUseCase;
    private final SuspendPriorityUseCase suspendUseCase;
    private final GetPriorityUseCase getUseCase;
    private final ListPriorityUseCase listUseCase;

    public PriorityController(
            CreatePriorityUseCase createUseCase,
            UpdatePriorityUseCase updateUseCase,
            ActivatePriorityUseCase activateUseCase,
            SuspendPriorityUseCase suspendUseCase,
            GetPriorityUseCase getUseCase,
            ListPriorityUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PriorityIdResponse create(@Valid @RequestBody CreatePriorityRequest request) {
        var id = createUseCase.execute(new CreatePriorityUseCase.CreatePriorityCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PriorityIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PriorityResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PriorityResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PriorityIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePriorityRequest request) {
        var updated = updateUseCase.execute(new UpdatePriorityUseCase.UpdatePriorityCommand(
                null, request.title(), request.description(), null, id, null));
        return new PriorityIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePriorityUseCase.ActivatePriorityCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPriorityRequest request) {
        suspendUseCase.execute(new SuspendPriorityUseCase.SuspendPriorityCommand(
                null, null, null, null, id, request.reason()));
    }

    private PriorityResponse toResponse(Priority aggregate) {
        return new PriorityResponse(
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

    public record CreatePriorityRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePriorityRequest(String title, String description) {}

    public record SuspendPriorityRequest(@NotBlank String reason) {}

    public record PriorityIdResponse(String id) {}

    public record PriorityResponse(
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
