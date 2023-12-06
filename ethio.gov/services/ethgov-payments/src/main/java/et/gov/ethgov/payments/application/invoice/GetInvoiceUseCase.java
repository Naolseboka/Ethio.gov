package et.gov.ethgov.payments.application.invoice;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceId;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import java.util.Objects;

public final class GetInvoiceUseCase {
    private final InvoiceRepository repository;

    public GetInvoiceUseCase(InvoiceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Invoice execute(String rawId) {
        InvoiceId id = InvoiceId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invoice not found: " + rawId));
    }
}
