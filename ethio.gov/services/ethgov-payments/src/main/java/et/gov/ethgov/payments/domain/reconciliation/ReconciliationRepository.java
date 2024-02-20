package et.gov.ethgov.payments.domain.reconciliation;

import java.util.List;
import java.util.Optional;

public interface ReconciliationRepository {
    Reconciliation save(Reconciliation aggregate);

    Optional<Reconciliation> findById(ReconciliationId id);

    Optional<Reconciliation> findByReferenceCode(String tenantId, String referenceCode);

    List<Reconciliation> findByTenant(String tenantId, int limit);

    void delete(ReconciliationId id);
}
