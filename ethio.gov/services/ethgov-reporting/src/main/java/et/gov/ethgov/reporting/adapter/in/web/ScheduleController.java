package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.schedule.ActivateScheduleUseCase;
import et.gov.ethgov.reporting.application.schedule.CreateScheduleUseCase;
import et.gov.ethgov.reporting.application.schedule.GetScheduleUseCase;
import et.gov.ethgov.reporting.application.schedule.ListScheduleUseCase;
import et.gov.ethgov.reporting.application.schedule.SuspendScheduleUseCase;
import et.gov.ethgov.reporting.application.schedule.UpdateScheduleUseCase;
import et.gov.ethgov.reporting.domain.schedule.Schedule;
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
@RequestMapping("/v1/reporting/schedule")
public class ScheduleController {
    private final CreateScheduleUseCase createUseCase;
    private final UpdateScheduleUseCase updateUseCase;
    private final ActivateScheduleUseCase activateUseCase;
    private final SuspendScheduleUseCase suspendUseCase;
    private final GetScheduleUseCase getUseCase;
    private final ListScheduleUseCase listUseCase;

    public ScheduleController(
            CreateScheduleUseCase createUseCase,
            UpdateScheduleUseCase updateUseCase,
            ActivateScheduleUseCase activateUseCase,
            SuspendScheduleUseCase suspendUseCase,
            GetScheduleUseCase getUseCase,
            ListScheduleUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleIdResponse create(@Valid @RequestBody CreateScheduleRequest request) {
        var id = createUseCase.execute(new CreateScheduleUseCase.CreateScheduleCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new ScheduleIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public ScheduleResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<ScheduleResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public ScheduleIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateScheduleRequest request) {
        var updated = updateUseCase.execute(new UpdateScheduleUseCase.UpdateScheduleCommand(
                null, request.title(), request.description(), null, id, null));
        return new ScheduleIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateScheduleUseCase.ActivateScheduleCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendScheduleRequest request) {
        suspendUseCase.execute(new SuspendScheduleUseCase.SuspendScheduleCommand(
                null, null, null, null, id, request.reason()));
    }

    private ScheduleResponse toResponse(Schedule aggregate) {
        return new ScheduleResponse(
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

    public record CreateScheduleRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateScheduleRequest(String title, String description) {}

    public record SuspendScheduleRequest(@NotBlank String reason) {}

    public record ScheduleIdResponse(String id) {}

    public record ScheduleResponse(
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
