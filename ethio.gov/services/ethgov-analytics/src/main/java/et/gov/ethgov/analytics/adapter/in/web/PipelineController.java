package et.gov.ethgov.analytics.adapter.in.web;

import et.gov.ethgov.analytics.application.pipeline.ActivatePipelineUseCase;
import et.gov.ethgov.analytics.application.pipeline.CreatePipelineUseCase;
import et.gov.ethgov.analytics.application.pipeline.GetPipelineUseCase;
import et.gov.ethgov.analytics.application.pipeline.ListPipelineUseCase;
import et.gov.ethgov.analytics.application.pipeline.SuspendPipelineUseCase;
import et.gov.ethgov.analytics.application.pipeline.UpdatePipelineUseCase;
import et.gov.ethgov.analytics.domain.pipeline.Pipeline;
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
@RequestMapping("/v1/analytics/pipeline")
public class PipelineController {
    private final CreatePipelineUseCase createUseCase;
    private final UpdatePipelineUseCase updateUseCase;
    private final ActivatePipelineUseCase activateUseCase;
    private final SuspendPipelineUseCase suspendUseCase;
    private final GetPipelineUseCase getUseCase;
    private final ListPipelineUseCase listUseCase;

    public PipelineController(
            CreatePipelineUseCase createUseCase,
            UpdatePipelineUseCase updateUseCase,
            ActivatePipelineUseCase activateUseCase,
            SuspendPipelineUseCase suspendUseCase,
            GetPipelineUseCase getUseCase,
            ListPipelineUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PipelineIdResponse create(@Valid @RequestBody CreatePipelineRequest request) {
        var id = createUseCase.execute(new CreatePipelineUseCase.CreatePipelineCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new PipelineIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public PipelineResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<PipelineResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public PipelineIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdatePipelineRequest request) {
        var updated = updateUseCase.execute(new UpdatePipelineUseCase.UpdatePipelineCommand(
                null, request.title(), request.description(), null, id, null));
        return new PipelineIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivatePipelineUseCase.ActivatePipelineCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendPipelineRequest request) {
        suspendUseCase.execute(new SuspendPipelineUseCase.SuspendPipelineCommand(
                null, null, null, null, id, request.reason()));
    }

    private PipelineResponse toResponse(Pipeline aggregate) {
        return new PipelineResponse(
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

    public record CreatePipelineRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdatePipelineRequest(String title, String description) {}

    public record SuspendPipelineRequest(@NotBlank String reason) {}

    public record PipelineIdResponse(String id) {}

    public record PipelineResponse(
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
