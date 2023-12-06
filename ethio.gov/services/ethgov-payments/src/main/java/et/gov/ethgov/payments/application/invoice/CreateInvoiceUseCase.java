package et.gov.ethgov.payments.application.invoice;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceId;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateInvoiceUseCase {
    private final InvoiceRepository repository;
    private final Clock clock;

    public CreateInvoiceUseCase(InvoiceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InvoiceId execute(CreateInvoiceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Invoice aggregate = Invoice.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateInvoiceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String invoiceId,
            String reason) {}
}
