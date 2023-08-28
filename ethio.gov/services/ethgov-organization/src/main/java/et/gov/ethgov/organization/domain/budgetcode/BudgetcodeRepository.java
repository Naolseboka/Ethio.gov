package et.gov.ethgov.organization.domain.budgetcode;

import java.util.List;
import java.util.Optional;

public interface BudgetcodeRepository {
    Budgetcode save(Budgetcode aggregate);

    Optional<Budgetcode> findById(BudgetcodeId id);

    Optional<Budgetcode> findByReferenceCode(String tenantId, String referenceCode);

    List<Budgetcode> findByTenant(String tenantId, int limit);

    void delete(BudgetcodeId id);
}
