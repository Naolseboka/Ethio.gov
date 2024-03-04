package et.gov.ethgov.payments.domain.settlement;

import java.util.List;
import java.util.Optional;

public interface SettlementRepository {
    Settlement save(Settlement aggregate);

    Optional<Settlement> findById(SettlementId id);

    Optional<Settlement> findByReferenceCode(String tenantId, String referenceCode);

    List<Settlement> findByTenant(String tenantId, int limit);

    void delete(SettlementId id);
}
