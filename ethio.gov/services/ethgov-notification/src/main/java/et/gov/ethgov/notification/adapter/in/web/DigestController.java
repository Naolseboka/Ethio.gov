package et.gov.ethgov.notification.adapter.in.web;

import et.gov.ethgov.notification.application.digest.ActivateDigestUseCase;
import et.gov.ethgov.notification.application.digest.CreateDigestUseCase;
import et.gov.ethgov.notification.application.digest.GetDigestUseCase;
import et.gov.ethgov.notification.application.digest.ListDigestUseCase;
import et.gov.ethgov.notification.application.digest.SuspendDigestUseCase;
import et.gov.ethgov.notification.application.digest.UpdateDigestUseCase;
import et.gov.ethgov.notification.domain.digest.Digest;
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
@RequestMapping("/v1/notification/digest")
public class DigestController {
    private final CreateDigestUseCase createUseCase;
    private final UpdateDigestUseCase updateUseCase;
    private final ActivateDigestUseCase activateUseCase;
    private final SuspendDigestUseCase suspendUseCase;
    private final GetDigestUseCase getUseCase;
    private final ListDigestUseCase listUseCase;

    public DigestController(
            CreateDigestUseCase createUseCase,
            UpdateDigestUseCase updateUseCase,
            ActivateDigestUseCase activateUseCase,
            SuspendDigestUseCase suspendUseCase,
            GetDigestUseCase getUseCase,
            ListDigestUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DigestIdResponse create(@Valid @RequestBody CreateDigestRequest request) {
        var id = createUseCase.execute(new CreateDigestUseCase.CreateDigestCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DigestIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DigestResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DigestResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DigestIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDigestRequest request) {
        var updated = updateUseCase.execute(new UpdateDigestUseCase.UpdateDigestCommand(
                null, request.title(), request.description(), null, id, null));
        return new DigestIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDigestUseCase.ActivateDigestCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDigestRequest request) {
        suspendUseCase.execute(new SuspendDigestUseCase.SuspendDigestCommand(
                null, null, null, null, id, request.reason()));
    }

    private DigestResponse toResponse(Digest aggregate) {
        return new DigestResponse(
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

    public record CreateDigestRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDigestRequest(String title, String description) {}

    public record SuspendDigestRequest(@NotBlank String reason) {}

    public record DigestIdResponse(String id) {}

    public record DigestResponse(
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
