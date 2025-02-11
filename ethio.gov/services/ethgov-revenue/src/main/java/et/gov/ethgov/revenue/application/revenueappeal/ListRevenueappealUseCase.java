package et.gov.ethgov.revenue.application.revenueappeal;

import et.gov.ethgov.revenue.domain.revenueappeal.Revenueappeal;
import et.gov.ethgov.revenue.domain.revenueappeal.RevenueappealRepository;
import java.util.List;
import java.util.Objects;

public final class ListRevenueappealUseCase {
    private final RevenueappealRepository repository;

    public ListRevenueappealUseCase(RevenueappealRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Revenueappeal> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
