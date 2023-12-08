package et.gov.ethgov.payments.application.invoice;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceId;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendInvoiceUseCase {
    private final InvoiceRepository repository;
    private final Clock clock;

    public SuspendInvoiceUseCase(InvoiceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InvoiceId execute(SuspendInvoiceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InvoiceId id = InvoiceId.parse(command.invoiceId());
        Invoice aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invoice not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendInvoiceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String invoiceId,
            String reason) {}
}
