package et.gov.ethgov.payments.domain.payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment aggregate);

    Optional<Payment> findById(PaymentId id);

    Optional<Payment> findByReferenceCode(String tenantId, String referenceCode);

    List<Payment> findByTenant(String tenantId, int limit);

    void delete(PaymentId id);
}
