package et.gov.ethgov.licensing.application.condition;

import et.gov.ethgov.licensing.domain.condition.Condition;
import et.gov.ethgov.licensing.domain.condition.ConditionRepository;
import java.util.List;
import java.util.Objects;

public final class ListConditionUseCase {
    private final ConditionRepository repository;

    public ListConditionUseCase(ConditionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Condition> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
