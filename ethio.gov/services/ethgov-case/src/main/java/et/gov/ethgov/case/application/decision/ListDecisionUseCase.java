package et.gov.ethgov.case.application.decision;

import et.gov.ethgov.case.domain.decision.Decision;
import et.gov.ethgov.case.domain.decision.DecisionRepository;
import java.util.List;
import java.util.Objects;

public final class ListDecisionUseCase {
    private final DecisionRepository repository;

    public ListDecisionUseCase(DecisionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Decision> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
