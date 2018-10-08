package et.gov.ethgov.analytics.application.scorecard;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import java.util.List;
import java.util.Objects;

public final class ListScorecardUseCase {
    private final ScorecardRepository repository;

    public ListScorecardUseCase(ScorecardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Scorecard> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
