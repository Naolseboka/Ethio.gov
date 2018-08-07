package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.sla.ActivateSlaUseCase;
import et.gov.ethgov.workflow.application.sla.CreateSlaUseCase;
import et.gov.ethgov.workflow.application.sla.GetSlaUseCase;
import et.gov.ethgov.workflow.application.sla.ListSlaUseCase;
import et.gov.ethgov.workflow.application.sla.SuspendSlaUseCase;
import et.gov.ethgov.workflow.application.sla.UpdateSlaUseCase;
import et.gov.ethgov.workflow.domain.sla.Sla;
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
@RequestMapping("/v1/workflow/sla")
public class SlaController {
    private final CreateSlaUseCase createUseCase;
    private final UpdateSlaUseCase updateUseCase;
    private final ActivateSlaUseCase activateUseCase;
    private final SuspendSlaUseCase suspendUseCase;
    private final GetSlaUseCase getUseCase;
    private final ListSlaUseCase listUseCase;

    public SlaController(
            CreateSlaUseCase createUseCase,
            UpdateSlaUseCase updateUseCase,
            ActivateSlaUseCase activateUseCase,
            SuspendSlaUseCase suspendUseCase,
            GetSlaUseCase getUseCase,
            ListSlaUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SlaIdResponse create(@Valid @RequestBody CreateSlaRequest request) {
        var id = createUseCase.execute(new CreateSlaUseCase.CreateSlaCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SlaIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SlaResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SlaResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SlaIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSlaRequest request) {
        var updated = updateUseCase.execute(new UpdateSlaUseCase.UpdateSlaCommand(
                null, request.title(), request.description(), null, id, null));
        return new SlaIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSlaUseCase.ActivateSlaCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSlaRequest request) {
        suspendUseCase.execute(new SuspendSlaUseCase.SuspendSlaCommand(
                null, null, null, null, id, request.reason()));
    }

    private SlaResponse toResponse(Sla aggregate) {
        return new SlaResponse(
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

    public record CreateSlaRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSlaRequest(String title, String description) {}

    public record SuspendSlaRequest(@NotBlank String reason) {}

    public record SlaIdResponse(String id) {}

    public record SlaResponse(
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
