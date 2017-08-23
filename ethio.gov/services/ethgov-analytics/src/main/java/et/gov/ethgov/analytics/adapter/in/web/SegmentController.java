package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.segment.ActivateSegmentUseCase;
import et.gov.ethgov.analytics.application.segment.CreateSegmentUseCase;
import et.gov.ethgov.analytics.application.segment.GetSegmentUseCase;
import et.gov.ethgov.analytics.application.segment.ListSegmentUseCase;
import et.gov.ethgov.analytics.application.segment.SuspendSegmentUseCase;
import et.gov.ethgov.analytics.application.segment.UpdateSegmentUseCase;
import et.gov.ethgov.analytics.domain.segment.Segment;
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
@RequestMapping("/v1/analytics/segment")
public class SegmentController {
    private final CreateSegmentUseCase createUseCase;
    private final UpdateSegmentUseCase updateUseCase;
    private final ActivateSegmentUseCase activateUseCase;
    private final SuspendSegmentUseCase suspendUseCase;
    private final GetSegmentUseCase getUseCase;
    private final ListSegmentUseCase listUseCase;

    public SegmentController(
            CreateSegmentUseCase createUseCase,
            UpdateSegmentUseCase updateUseCase,
            ActivateSegmentUseCase activateUseCase,
            SuspendSegmentUseCase suspendUseCase,
            GetSegmentUseCase getUseCase,
            ListSegmentUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SegmentIdResponse create(@Valid @RequestBody CreateSegmentRequest request) {
        var id = createUseCase.execute(new CreateSegmentUseCase.CreateSegmentCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new SegmentIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public SegmentResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<SegmentResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public SegmentIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateSegmentRequest request) {
        var updated = updateUseCase.execute(new UpdateSegmentUseCase.UpdateSegmentCommand(
                null, request.title(), request.description(), null, id, null));
        return new SegmentIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateSegmentUseCase.ActivateSegmentCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendSegmentRequest request) {
        suspendUseCase.execute(new SuspendSegmentUseCase.SuspendSegmentCommand(
                null, null, null, null, id, request.reason()));
    }

    private SegmentResponse toResponse(Segment aggregate) {
        return new SegmentResponse(
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

    public record CreateSegmentRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateSegmentRequest(String title, String description) {}

    public record SuspendSegmentRequest(@NotBlank String reason) {}

    public record SegmentIdResponse(String id) {}

    public record SegmentResponse(
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
