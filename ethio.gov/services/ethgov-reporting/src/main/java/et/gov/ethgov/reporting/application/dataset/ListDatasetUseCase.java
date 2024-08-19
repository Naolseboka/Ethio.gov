package et.gov.ethgov.reporting.application.dataset;

import et.gov.ethgov.reporting.domain.dataset.Dataset;
import et.gov.ethgov.reporting.domain.dataset.DatasetRepository;
import java.util.List;
import java.util.Objects;

public final class ListDatasetUseCase {
    private final DatasetRepository repository;

    public ListDatasetUseCase(DatasetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Dataset> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
