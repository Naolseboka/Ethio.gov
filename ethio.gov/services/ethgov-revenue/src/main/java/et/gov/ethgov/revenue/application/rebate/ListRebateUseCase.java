package et.gov.ethgov.revenue.application.rebate;

import et.gov.ethgov.revenue.domain.rebate.Rebate;
import et.gov.ethgov.revenue.domain.rebate.RebateRepository;
import java.util.List;
import java.util.Objects;

public final class ListRebateUseCase {
    private final RebateRepository repository;

    public ListRebateUseCase(RebateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Rebate> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
