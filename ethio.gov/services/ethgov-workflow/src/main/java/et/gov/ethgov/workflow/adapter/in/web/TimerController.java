package et.gov.ethgov.workflow.adapter.in.web;

import et.gov.ethgov.workflow.application.timer.ActivateTimerUseCase;
import et.gov.ethgov.workflow.application.timer.CreateTimerUseCase;
import et.gov.ethgov.workflow.application.timer.GetTimerUseCase;
import et.gov.ethgov.workflow.application.timer.ListTimerUseCase;
import et.gov.ethgov.workflow.application.timer.SuspendTimerUseCase;
import et.gov.ethgov.workflow.application.timer.UpdateTimerUseCase;
import et.gov.ethgov.workflow.domain.timer.Timer;
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
@RequestMapping("/v1/workflow/timer")
public class TimerController {
    private final CreateTimerUseCase createUseCase;
    private final UpdateTimerUseCase updateUseCase;
    private final ActivateTimerUseCase activateUseCase;
    private final SuspendTimerUseCase suspendUseCase;
    private final GetTimerUseCase getUseCase;
    private final ListTimerUseCase listUseCase;

    public TimerController(
            CreateTimerUseCase createUseCase,
            UpdateTimerUseCase updateUseCase,
            ActivateTimerUseCase activateUseCase,
            SuspendTimerUseCase suspendUseCase,
            GetTimerUseCase getUseCase,
            ListTimerUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TimerIdResponse create(@Valid @RequestBody CreateTimerRequest request) {
        var id = createUseCase.execute(new CreateTimerUseCase.CreateTimerCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new TimerIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public TimerResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<TimerResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public TimerIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateTimerRequest request) {
        var updated = updateUseCase.execute(new UpdateTimerUseCase.UpdateTimerCommand(
                null, request.title(), request.description(), null, id, null));
        return new TimerIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateTimerUseCase.ActivateTimerCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendTimerRequest request) {
        suspendUseCase.execute(new SuspendTimerUseCase.SuspendTimerCommand(
                null, null, null, null, id, request.reason()));
    }

    private TimerResponse toResponse(Timer aggregate) {
        return new TimerResponse(
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

    public record CreateTimerRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateTimerRequest(String title, String description) {}

    public record SuspendTimerRequest(@NotBlank String reason) {}

    public record TimerIdResponse(String id) {}

    public record TimerResponse(
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
