package et.gov.ethgov.payments.domain.receipt;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository {
    Receipt save(Receipt aggregate);

    Optional<Receipt> findById(ReceiptId id);

    Optional<Receipt> findByReferenceCode(String tenantId, String referenceCode);

    List<Receipt> findByTenant(String tenantId, int limit);

    void delete(ReceiptId id);
}
