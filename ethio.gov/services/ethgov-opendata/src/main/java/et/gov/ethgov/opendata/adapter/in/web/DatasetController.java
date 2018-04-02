package et.gov.ethgov.opendata.adapter.in.web;

import et.gov.ethgov.opendata.application.dataset.CancelDatasetUseCase;
import et.gov.ethgov.opendata.application.dataset.GetDatasetUseCase;
import et.gov.ethgov.opendata.application.dataset.PublishDatasetUseCase;
import et.gov.ethgov.opendata.application.dataset.RegisterDatasetUseCase;
import et.gov.ethgov.opendata.application.dataset.RetireDatasetUseCase;
import et.gov.ethgov.opendata.application.dataset.UpdateDatasetUseCase;
import et.gov.ethgov.opendata.domain.dataset.OpenDataset;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@RequestMapping("/v1/datasets")
public class DatasetController {

    private final RegisterDatasetUseCase registerDataset;
    private final UpdateDatasetUseCase updateDataset;
    private final PublishDatasetUseCase publishDataset;
    private final RetireDatasetUseCase retireDataset;
    private final CancelDatasetUseCase cancelDataset;
    private final GetDatasetUseCase getDataset;

    public DatasetController(
            RegisterDatasetUseCase registerDataset,
            UpdateDatasetUseCase updateDataset,
            PublishDatasetUseCase publishDataset,
            RetireDatasetUseCase retireDataset,
            CancelDatasetUseCase cancelDataset,
            GetDatasetUseCase getDataset) {
        this.registerDataset = registerDataset;
        this.updateDataset = updateDataset;
        this.publishDataset = publishDataset;
        this.retireDataset = retireDataset;
        this.cancelDataset = cancelDataset;
        this.getDataset = getDataset;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetIdResponse register(@Valid @RequestBody RegisterRequest request) {
        return new DatasetIdResponse(registerDataset
                .execute(new RegisterDatasetUseCase.Command(
                        request.tenantId(),
                        request.slug(),
                        request.title(),
                        request.description(),
                        request.theme(),
                        request.license(),
                        request.publisherOrgId(),
                        request.contactEmail(),
                        request.locale()))
                .value());
    }

    @PutMapping("/{datasetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable String datasetId, @Valid @RequestBody UpdateRequest request) {
        updateDataset.execute(new UpdateDatasetUseCase.Command(
                datasetId,
                request.title(),
                request.description(),
                request.theme(),
                request.license(),
                request.publisherOrgId(),
                request.contactEmail(),
                request.locale()));
    }

    @GetMapping("/lookup")
    public DatasetResponse lookup(
            @RequestParam String tenantId,
            @RequestParam(required = false) String datasetNumber,
            @RequestParam(required = false) String slug) {
        if (slug != null && !slug.isBlank()) {
            return DatasetResponse.from(getDataset.bySlug(tenantId, slug));
        }
        return DatasetResponse.from(getDataset.byNumber(tenantId, datasetNumber));
    }

    @GetMapping("/{datasetId}")
    public DatasetResponse get(@PathVariable String datasetId) {
        return DatasetResponse.from(getDataset.byId(datasetId));
    }

    @PostMapping("/{datasetId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(@PathVariable String datasetId) {
        publishDataset.execute(datasetId);
    }

    @PostMapping("/{datasetId}/retire")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retire(@PathVariable String datasetId) {
        retireDataset.execute(datasetId);
    }

    @PostMapping("/{datasetId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String datasetId, @Valid @RequestBody ReasonRequest request) {
        cancelDataset.execute(datasetId, request.reason());
    }

    public record RegisterRequest(
            @NotBlank String tenantId,
            @NotBlank String slug,
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(min = 10, max = 4000) String description,
            @NotBlank String theme,
            @NotBlank String license,
            String publisherOrgId,
            String contactEmail,
            String locale) {}

    public record UpdateRequest(
            @NotBlank @Size(max = 200) String title,
            @NotBlank @Size(min = 10, max = 4000) String description,
            @NotBlank String theme,
            @NotBlank String license,
            String publisherOrgId,
            String contactEmail,
            String locale) {}

    public record ReasonRequest(@NotBlank @Size(min = 5, max = 500) String reason) {}

    public record DatasetIdResponse(String datasetId) {}

    public record DatasetResponse(
            String datasetId,
            String tenantId,
            String datasetNumber,
            String slug,
            String title,
            String description,
            String theme,
            String license,
            String publisherOrgId,
            String contactEmail,
            String locale,
            String status) {

        static DatasetResponse from(OpenDataset dataset) {
            return new DatasetResponse(
                    dataset.id().value(),
                    dataset.tenantId().value(),
                    dataset.datasetNumber().value(),
                    dataset.slug().value(),
                    dataset.title(),
                    dataset.description(),
                    dataset.theme().name(),
                    dataset.license().name(),
                    dataset.publisherOrgId().orElse(null),
                    dataset.contactEmail().orElse(null),
                    dataset.locale(),
                    dataset.status().name());
        }
    }
}
