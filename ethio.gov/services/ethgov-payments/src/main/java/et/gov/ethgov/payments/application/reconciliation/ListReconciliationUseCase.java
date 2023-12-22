package et.gov.ethgov.payments.application.reconciliation;

import et.gov.ethgov.payments.domain.reconciliation.Reconciliation;
import et.gov.ethgov.payments.domain.reconciliation.ReconciliationRepository;
import java.util.List;
import java.util.Objects;

public final class ListReconciliationUseCase {
    private final ReconciliationRepository repository;

    public ListReconciliationUseCase(ReconciliationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Reconciliation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
