package et.gov.ethgov.document.adapter.in.web;

import et.gov.ethgov.document.application.retention.ActivateRetentionUseCase;
import et.gov.ethgov.document.application.retention.CreateRetentionUseCase;
import et.gov.ethgov.document.application.retention.GetRetentionUseCase;
import et.gov.ethgov.document.application.retention.ListRetentionUseCase;
import et.gov.ethgov.document.application.retention.SuspendRetentionUseCase;
import et.gov.ethgov.document.application.retention.UpdateRetentionUseCase;
import et.gov.ethgov.document.domain.retention.Retention;
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
@RequestMapping("/v1/document/retention")
public class RetentionController {
    private final CreateRetentionUseCase createUseCase;
    private final UpdateRetentionUseCase updateUseCase;
    private final ActivateRetentionUseCase activateUseCase;
    private final SuspendRetentionUseCase suspendUseCase;
    private final GetRetentionUseCase getUseCase;
    private final ListRetentionUseCase listUseCase;

    public RetentionController(
            CreateRetentionUseCase createUseCase,
            UpdateRetentionUseCase updateUseCase,
            ActivateRetentionUseCase activateUseCase,
            SuspendRetentionUseCase suspendUseCase,
            GetRetentionUseCase getUseCase,
            ListRetentionUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RetentionIdResponse create(@Valid @RequestBody CreateRetentionRequest request) {
        var id = createUseCase.execute(new CreateRetentionUseCase.CreateRetentionCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new RetentionIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public RetentionResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<RetentionResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public RetentionIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateRetentionRequest request) {
        var updated = updateUseCase.execute(new UpdateRetentionUseCase.UpdateRetentionCommand(
                null, request.title(), request.description(), null, id, null));
        return new RetentionIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateRetentionUseCase.ActivateRetentionCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendRetentionRequest request) {
        suspendUseCase.execute(new SuspendRetentionUseCase.SuspendRetentionCommand(
                null, null, null, null, id, request.reason()));
    }

    private RetentionResponse toResponse(Retention aggregate) {
        return new RetentionResponse(
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

    public record CreateRetentionRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateRetentionRequest(String title, String description) {}

    public record SuspendRetentionRequest(@NotBlank String reason) {}

    public record RetentionIdResponse(String id) {}

    public record RetentionResponse(
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
