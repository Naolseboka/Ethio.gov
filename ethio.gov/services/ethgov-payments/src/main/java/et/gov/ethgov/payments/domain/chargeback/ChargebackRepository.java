package et.gov.ethgov.payments.domain.chargeback;

import java.util.List;
import java.util.Optional;

public interface ChargebackRepository {
    Chargeback save(Chargeback aggregate);

    Optional<Chargeback> findById(ChargebackId id);

    Optional<Chargeback> findByReferenceCode(String tenantId, String referenceCode);

    List<Chargeback> findByTenant(String tenantId, int limit);

    void delete(ChargebackId id);
}
