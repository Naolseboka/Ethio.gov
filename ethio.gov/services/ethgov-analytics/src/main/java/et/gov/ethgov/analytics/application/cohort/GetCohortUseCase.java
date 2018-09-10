package et.gov.ethgov.analytics.application.cohort;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortId;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import java.util.Objects;

public final class GetCohortUseCase {
    private final CohortRepository repository;

    public GetCohortUseCase(CohortRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Cohort execute(String rawId) {
        CohortId id = CohortId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cohort not found: " + rawId));
    }
}
