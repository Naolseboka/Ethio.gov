package et.gov.ethgov.payments.application.invoice;

import et.gov.ethgov.payments.domain.invoice.Invoice;
import et.gov.ethgov.payments.domain.invoice.InvoiceId;
import et.gov.ethgov.payments.domain.invoice.InvoiceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateInvoiceUseCase {
    private final InvoiceRepository repository;
    private final Clock clock;

    public UpdateInvoiceUseCase(InvoiceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InvoiceId execute(UpdateInvoiceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InvoiceId id = InvoiceId.parse(command.invoiceId());
        Invoice aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invoice not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateInvoiceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String invoiceId,
            String reason) {}
}
