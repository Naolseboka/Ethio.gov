package et.gov.ethgov.payments.domain.paymentplan;

import java.util.List;
import java.util.Optional;

public interface PaymentplanRepository {
    Paymentplan save(Paymentplan aggregate);

    Optional<Paymentplan> findById(PaymentplanId id);

    Optional<Paymentplan> findByReferenceCode(String tenantId, String referenceCode);

    List<Paymentplan> findByTenant(String tenantId, int limit);

    void delete(PaymentplanId id);
}
