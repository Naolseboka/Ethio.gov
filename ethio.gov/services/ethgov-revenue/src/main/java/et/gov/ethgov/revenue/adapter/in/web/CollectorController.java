package et.gov.ethgov.revenue.adapter.in.web;

import et.gov.ethgov.revenue.application.collector.ActivateCollectorUseCase;
import et.gov.ethgov.revenue.application.collector.CreateCollectorUseCase;
import et.gov.ethgov.revenue.application.collector.GetCollectorUseCase;
import et.gov.ethgov.revenue.application.collector.ListCollectorUseCase;
import et.gov.ethgov.revenue.application.collector.SuspendCollectorUseCase;
import et.gov.ethgov.revenue.application.collector.UpdateCollectorUseCase;
import et.gov.ethgov.revenue.domain.collector.Collector;
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
@RequestMapping("/v1/revenue/collector")
public class CollectorController {
    private final CreateCollectorUseCase createUseCase;
    private final UpdateCollectorUseCase updateUseCase;
    private final ActivateCollectorUseCase activateUseCase;
    private final SuspendCollectorUseCase suspendUseCase;
    private final GetCollectorUseCase getUseCase;
    private final ListCollectorUseCase listUseCase;

    public CollectorController(
            CreateCollectorUseCase createUseCase,
            UpdateCollectorUseCase updateUseCase,
            ActivateCollectorUseCase activateUseCase,
            SuspendCollectorUseCase suspendUseCase,
            GetCollectorUseCase getUseCase,
            ListCollectorUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CollectorIdResponse create(@Valid @RequestBody CreateCollectorRequest request) {
        var id = createUseCase.execute(new CreateCollectorUseCase.CreateCollectorCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new CollectorIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public CollectorResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<CollectorResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public CollectorIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateCollectorRequest request) {
        var updated = updateUseCase.execute(new UpdateCollectorUseCase.UpdateCollectorCommand(
                null, request.title(), request.description(), null, id, null));
        return new CollectorIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateCollectorUseCase.ActivateCollectorCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendCollectorRequest request) {
        suspendUseCase.execute(new SuspendCollectorUseCase.SuspendCollectorCommand(
                null, null, null, null, id, request.reason()));
    }

    private CollectorResponse toResponse(Collector aggregate) {
        return new CollectorResponse(
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

    public record CreateCollectorRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateCollectorRequest(String title, String description) {}

    public record SuspendCollectorRequest(@NotBlank String reason) {}

    public record CollectorIdResponse(String id) {}

    public record CollectorResponse(
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
