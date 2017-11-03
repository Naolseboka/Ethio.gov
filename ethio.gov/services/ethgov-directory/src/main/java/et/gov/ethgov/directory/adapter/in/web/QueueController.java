package et.gov.ethgov.directory.adapter.in.web;

import et.gov.ethgov.directory.application.queue.ActivateQueueUseCase;
import et.gov.ethgov.directory.application.queue.CreateQueueUseCase;
import et.gov.ethgov.directory.application.queue.GetQueueUseCase;
import et.gov.ethgov.directory.application.queue.ListQueueUseCase;
import et.gov.ethgov.directory.application.queue.SuspendQueueUseCase;
import et.gov.ethgov.directory.application.queue.UpdateQueueUseCase;
import et.gov.ethgov.directory.domain.queue.Queue;
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
@RequestMapping("/v1/directory/queue")
public class QueueController {
    private final CreateQueueUseCase createUseCase;
    private final UpdateQueueUseCase updateUseCase;
    private final ActivateQueueUseCase activateUseCase;
    private final SuspendQueueUseCase suspendUseCase;
    private final GetQueueUseCase getUseCase;
    private final ListQueueUseCase listUseCase;

    public QueueController(
            CreateQueueUseCase createUseCase,
            UpdateQueueUseCase updateUseCase,
            ActivateQueueUseCase activateUseCase,
            SuspendQueueUseCase suspendUseCase,
            GetQueueUseCase getUseCase,
            ListQueueUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QueueIdResponse create(@Valid @RequestBody CreateQueueRequest request) {
        var id = createUseCase.execute(new CreateQueueUseCase.CreateQueueCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new QueueIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public QueueResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<QueueResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public QueueIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateQueueRequest request) {
        var updated = updateUseCase.execute(new UpdateQueueUseCase.UpdateQueueCommand(
                null, request.title(), request.description(), null, id, null));
        return new QueueIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateQueueUseCase.ActivateQueueCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendQueueRequest request) {
        suspendUseCase.execute(new SuspendQueueUseCase.SuspendQueueCommand(
                null, null, null, null, id, request.reason()));
    }

    private QueueResponse toResponse(Queue aggregate) {
        return new QueueResponse(
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

    public record CreateQueueRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateQueueRequest(String title, String description) {}

    public record SuspendQueueRequest(@NotBlank String reason) {}

    public record QueueIdResponse(String id) {}

    public record QueueResponse(
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
