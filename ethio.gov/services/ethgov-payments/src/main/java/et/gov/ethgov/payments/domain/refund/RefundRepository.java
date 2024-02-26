package et.gov.ethgov.payments.domain.refund;

import java.util.List;
import java.util.Optional;

public interface RefundRepository {
    Refund save(Refund aggregate);

    Optional<Refund> findById(RefundId id);

    Optional<Refund> findByReferenceCode(String tenantId, String referenceCode);

    List<Refund> findByTenant(String tenantId, int limit);

    void delete(RefundId id);
}
