package et.gov.ethgov.payments.application.invoice;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import java.util.List;
import java.util.Objects;

public final class ListInvoiceUseCase {
    private final InvoiceRepository repository;

    public ListInvoiceUseCase(InvoiceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Invoice> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
