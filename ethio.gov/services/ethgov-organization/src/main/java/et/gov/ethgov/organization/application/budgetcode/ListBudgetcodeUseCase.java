package et.gov.ethgov.organization.application.budgetcode;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import java.util.List;
import java.util.Objects;

public final class ListBudgetcodeUseCase {
    private final BudgetcodeRepository repository;

    public ListBudgetcodeUseCase(BudgetcodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Budgetcode> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
