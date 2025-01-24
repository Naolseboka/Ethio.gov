package et.gov.ethgov.revenue.application.fiscalyear;

import et.gov.ethgov.revenue.domain.fiscalyear.Fiscalyear;
import et.gov.ethgov.revenue.domain.fiscalyear.FiscalyearRepository;
import java.util.List;
import java.util.Objects;

public final class ListFiscalyearUseCase {
    private final FiscalyearRepository repository;

    public ListFiscalyearUseCase(FiscalyearRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Fiscalyear> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
