package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.quality.ActivateQualityUseCase;
import et.gov.ethgov.opendata.application.quality.CreateQualityUseCase;
import et.gov.ethgov.opendata.application.quality.GetQualityUseCase;
import et.gov.ethgov.opendata.application.quality.ListQualityUseCase;
import et.gov.ethgov.opendata.application.quality.SuspendQualityUseCase;
import et.gov.ethgov.opendata.application.quality.UpdateQualityUseCase;
import et.gov.ethgov.opendata.domain.quality.Quality;
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
@RequestMapping("/v1/opendata/quality")
public class QualityController {
    private final CreateQualityUseCase createUseCase;
    private final UpdateQualityUseCase updateUseCase;
    private final ActivateQualityUseCase activateUseCase;
    private final SuspendQualityUseCase suspendUseCase;
    private final GetQualityUseCase getUseCase;
    private final ListQualityUseCase listUseCase;

    public QualityController(
            CreateQualityUseCase createUseCase,
            UpdateQualityUseCase updateUseCase,
            ActivateQualityUseCase activateUseCase,
            SuspendQualityUseCase suspendUseCase,
            GetQualityUseCase getUseCase,
            ListQualityUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QualityIdResponse create(@Valid @RequestBody CreateQualityRequest request) {
        var id = createUseCase.execute(new CreateQualityUseCase.CreateQualityCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new QualityIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public QualityResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<QualityResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public QualityIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateQualityRequest request) {
        var updated = updateUseCase.execute(new UpdateQualityUseCase.UpdateQualityCommand(
                null, request.title(), request.description(), null, id, null));
        return new QualityIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateQualityUseCase.ActivateQualityCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendQualityRequest request) {
        suspendUseCase.execute(new SuspendQualityUseCase.SuspendQualityCommand(
                null, null, null, null, id, request.reason()));
    }

    private QualityResponse toResponse(Quality aggregate) {
        return new QualityResponse(
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

    public record CreateQualityRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateQualityRequest(String title, String description) {}

    public record SuspendQualityRequest(@NotBlank String reason) {}

    public record QualityIdResponse(String id) {}

    public record QualityResponse(
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
