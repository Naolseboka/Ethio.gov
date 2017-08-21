package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.event.ActivateEventUseCase;
import et.gov.ethgov.analytics.application.event.CreateEventUseCase;
import et.gov.ethgov.analytics.application.event.GetEventUseCase;
import et.gov.ethgov.analytics.application.event.ListEventUseCase;
import et.gov.ethgov.analytics.application.event.SuspendEventUseCase;
import et.gov.ethgov.analytics.application.event.UpdateEventUseCase;
import et.gov.ethgov.analytics.domain.event.Event;
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
@RequestMapping("/v1/analytics/event")
public class EventController {
    private final CreateEventUseCase createUseCase;
    private final UpdateEventUseCase updateUseCase;
    private final ActivateEventUseCase activateUseCase;
    private final SuspendEventUseCase suspendUseCase;
    private final GetEventUseCase getUseCase;
    private final ListEventUseCase listUseCase;

    public EventController(
            CreateEventUseCase createUseCase,
            UpdateEventUseCase updateUseCase,
            ActivateEventUseCase activateUseCase,
            SuspendEventUseCase suspendUseCase,
            GetEventUseCase getUseCase,
            ListEventUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventIdResponse create(@Valid @RequestBody CreateEventRequest request) {
        var id = createUseCase.execute(new CreateEventUseCase.CreateEventCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new EventIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public EventResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<EventResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public EventIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateEventRequest request) {
        var updated = updateUseCase.execute(new UpdateEventUseCase.UpdateEventCommand(
                null, request.title(), request.description(), null, id, null));
        return new EventIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateEventUseCase.ActivateEventCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendEventRequest request) {
        suspendUseCase.execute(new SuspendEventUseCase.SuspendEventCommand(
                null, null, null, null, id, request.reason()));
    }

    private EventResponse toResponse(Event aggregate) {
        return new EventResponse(
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

    public record CreateEventRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateEventRequest(String title, String description) {}

    public record SuspendEventRequest(@NotBlank String reason) {}

    public record EventIdResponse(String id) {}

    public record EventResponse(
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
