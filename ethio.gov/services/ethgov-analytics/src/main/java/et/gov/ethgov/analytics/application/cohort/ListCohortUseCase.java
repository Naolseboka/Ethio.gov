package et.gov.ethgov.analytics.application.cohort;

import et.gov.ethgov.analytics.domain.cohort.Cohort;
import et.gov.ethgov.analytics.domain.cohort.CohortRepository;
import java.util.List;
import java.util.Objects;

public final class ListCohortUseCase {
    private final CohortRepository repository;

    public ListCohortUseCase(CohortRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Cohort> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
