package et.gov.ethgov.organization.application.budgetcode;

import et.gov.ethgov.organization.domain.budgetcode.Budgetcode;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeId;
import et.gov.ethgov.organization.domain.budgetcode.BudgetcodeRepository;
import java.util.Objects;

public final class GetBudgetcodeUseCase {
    private final BudgetcodeRepository repository;

    public GetBudgetcodeUseCase(BudgetcodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Budgetcode execute(String rawId) {
        BudgetcodeId id = BudgetcodeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("budgetcode not found: " + rawId));
    }
}
