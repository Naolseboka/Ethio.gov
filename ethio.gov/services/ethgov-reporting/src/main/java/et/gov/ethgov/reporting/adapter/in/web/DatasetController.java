package et.gov.ethgov.reporting.adapter.in.web;

import et.gov.ethgov.reporting.application.dataset.ActivateDatasetUseCase;
import et.gov.ethgov.reporting.application.dataset.CreateDatasetUseCase;
import et.gov.ethgov.reporting.application.dataset.GetDatasetUseCase;
import et.gov.ethgov.reporting.application.dataset.ListDatasetUseCase;
import et.gov.ethgov.reporting.application.dataset.SuspendDatasetUseCase;
import et.gov.ethgov.reporting.application.dataset.UpdateDatasetUseCase;
import et.gov.ethgov.reporting.domain.dataset.Dataset;
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
@RequestMapping("/v1/reporting/dataset")
public class DatasetController {
    private final CreateDatasetUseCase createUseCase;
    private final UpdateDatasetUseCase updateUseCase;
    private final ActivateDatasetUseCase activateUseCase;
    private final SuspendDatasetUseCase suspendUseCase;
    private final GetDatasetUseCase getUseCase;
    private final ListDatasetUseCase listUseCase;

    public DatasetController(
            CreateDatasetUseCase createUseCase,
            UpdateDatasetUseCase updateUseCase,
            ActivateDatasetUseCase activateUseCase,
            SuspendDatasetUseCase suspendUseCase,
            GetDatasetUseCase getUseCase,
            ListDatasetUseCase listUseCase) {
        this.createUseCase = createUseCase;
        this.updateUseCase = updateUseCase;
        this.activateUseCase = activateUseCase;
        this.suspendUseCase = suspendUseCase;
        this.getUseCase = getUseCase;
        this.listUseCase = listUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetIdResponse create(@Valid @RequestBody CreateDatasetRequest request) {
        var id = createUseCase.execute(new CreateDatasetUseCase.CreateDatasetCommand(
                request.referenceCode(), request.title(), request.description(), request.tenantId(), null, null));
        return new DatasetIdResponse(id.toString());
    }

    @GetMapping("/{id}")
    public DatasetResponse get(@PathVariable("id") String id) {
        return toResponse(getUseCase.execute(id));
    }

    @GetMapping
    public List<DatasetResponse> list(
            @RequestParam("tenantId") String tenantId,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return listUseCase.execute(tenantId, limit).stream().map(this::toResponse).toList();
    }

    @PutMapping("/{id}")
    public DatasetIdResponse update(@PathVariable("id") String id, @Valid @RequestBody UpdateDatasetRequest request) {
        var updated = updateUseCase.execute(new UpdateDatasetUseCase.UpdateDatasetCommand(
                null, request.title(), request.description(), null, id, null));
        return new DatasetIdResponse(updated.toString());
    }

    @PostMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable("id") String id) {
        activateUseCase.execute(new ActivateDatasetUseCase.ActivateDatasetCommand(
                null, null, null, null, id, null));
    }

    @PostMapping("/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspend(@PathVariable("id") String id, @Valid @RequestBody SuspendDatasetRequest request) {
        suspendUseCase.execute(new SuspendDatasetUseCase.SuspendDatasetCommand(
                null, null, null, null, id, request.reason()));
    }

    private DatasetResponse toResponse(Dataset aggregate) {
        return new DatasetResponse(
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

    public record CreateDatasetRequest(
            @NotBlank String referenceCode,
            @NotBlank String title,
            String description,
            @NotBlank String tenantId) {}

    public record UpdateDatasetRequest(String title, String description) {}

    public record SuspendDatasetRequest(@NotBlank String reason) {}

    public record DatasetIdResponse(String id) {}

    public record DatasetResponse(
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
