package et.gov.ethgov.analytics.application.experiment;

import et.gov.ethgov.analytics.domain.experiment.Experiment;
import et.gov.ethgov.analytics.domain.experiment.ExperimentId;
import et.gov.ethgov.analytics.domain.experiment.ExperimentRepository;
import java.util.Objects;

public final class GetExperimentUseCase {
    private final ExperimentRepository repository;

    public GetExperimentUseCase(ExperimentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Experiment execute(String rawId) {
        ExperimentId id = ExperimentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("experiment not found: " + rawId));
    }
}
