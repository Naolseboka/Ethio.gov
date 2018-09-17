package et.gov.ethgov.analytics.application.experiment;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import java.util.List;
import java.util.Objects;

public final class ListExperimentUseCase {
    private final ExperimentRepository repository;

    public ListExperimentUseCase(ExperimentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Experiment> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
