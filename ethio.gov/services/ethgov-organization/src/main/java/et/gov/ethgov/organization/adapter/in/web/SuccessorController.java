package et.gov.ethgov.organization.adapter.in.web;

import et.gov.ethgov.organization.application.successor.ActivateSuccessorUseCase;
import et.gov.ethgov.organization.application.successor.CreateSuccessorUseCase;
import et.gov.ethgov.organization.application.successor.GetSuccessorUseCase;
import et.gov.ethgov.organization.application.successor.ListSuccessorUseCase;
import et.gov.ethgov.organization.application.successor.SuspendSuccessorUseCase;
import et.gov.ethgov.organization.application.successor.UpdateSuccessorUseCase;
import et.gov.ethgov.organization.domain.successor.Successor;
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
@RequestMapping("/v1/organization/successor")
public class SuccessorController {
    private final CreateSuccessorUseCase createUseCase;
    private final UpdateSuccessorUseCase updateUseCase;
    private final ActivateSuccessorUseCase activateUseCase;
    private final SuspendSuccessorUseCase suspendUseCase;
    private final GetSuccessorUseCase getUseCase;
    private final ListSuccessorUseCase listUseCase;

    public SuccessorController(
            CreateSuccessorUseCase createUseCase,
            UpdateSuccessorUseCase updateUseCase,
            ActivateSuccessorUseCase activateUseCase,
            SuspendSuccessorUseCase suspendUseCase,
            GetSuccessorUseCase getUseCase,
            ListSuccessorUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuccessorIdResponse create(@Valid @RequestBody CreateSuccessorRequest request) {
        var id = createUseCase.execute(new CreateSuccessorUseCase.CreateSuccessorCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SuccessorIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SuccessorResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SuccessorResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SuccessorIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSuccessorRequest request) {
        var updated = updateUseCase.execute(new UpdateSuccessorUseCase.UpdateSuccessorCommand(
                null, request.title(), request.description(), null, id, null));
        return new SuccessorIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSuccessorUseCase.ActivateSuccessorCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSuccessorRequest request) {
        suspendUseCase.execute(new SuspendSuccessorUseCase.SuspendSuccessorCommand(
                null, null, null, null, id, request.reason()));
    }

    private SuccessorResponse toResponse(Successor aggregate) {
        return new SuccessorResponse(
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

    public record CreateSuccessorRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSuccessorRequest(String title, String description) {}

    public record SuspendSuccessorRequest(@NotBlank String reason) {}

    public record SuccessorIdResponse(String id) {}

    public record SuccessorResponse(
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
