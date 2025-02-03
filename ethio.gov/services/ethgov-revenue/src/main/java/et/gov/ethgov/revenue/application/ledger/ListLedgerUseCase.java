package et.gov.ethgov.revenue.application.ledger;

import et.gov.ethgov.revenue.domain.ledger.Ledger;
import et.gov.ethgov.revenue.domain.ledger.LedgerRepository;
import java.util.List;
import java.util.Objects;

public final class ListLedgerUseCase {
    private final LedgerRepository repository;

    public ListLedgerUseCase(LedgerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Ledger> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
