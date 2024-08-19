package et.gov.ethgov.reporting.application.dataset;

import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetId;
import et.gov.ethgov.reporting.domain.dataset.DatasetRepository;
import java.util.Objects;

public final class GetDatasetUseCase {
    private final DatasetRepository repository;

    public GetDatasetUseCase(DatasetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Dataset execute(String rawId) {
        DatasetId id = DatasetId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dataset not found: " + rawId));
    }
}
