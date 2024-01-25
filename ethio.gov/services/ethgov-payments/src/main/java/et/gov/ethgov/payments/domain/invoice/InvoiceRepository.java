package et.gov.ethgov.payments.domain.invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository {
    Invoice save(Invoice aggregate);

    Optional<Invoice> findById(InvoiceId id);

    Optional<Invoice> findByReferenceCode(String tenantId, String referenceCode);

    List<Invoice> findByTenant(String tenantId, int limit);

    void delete(InvoiceId id);
}
