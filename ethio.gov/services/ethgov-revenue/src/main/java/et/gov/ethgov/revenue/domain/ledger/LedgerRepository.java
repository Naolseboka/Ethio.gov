package et.gov.ethgov.revenue.domain.ledger;

import java.util.List;
import java.util.Optional;

public interface LedgerRepository {
    Ledger save(Ledger aggregate);

    Optional<Ledger> findById(LedgerId id);

    Optional<Ledger> findByReferenceCode(String tenantId, String referenceCode);

    List<Ledger> findByTenant(String tenantId, int limit);

    void delete(LedgerId id);
}
