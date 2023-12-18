package et.gov.ethgov.payments.application.receipt;

import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptId;
import et.gov.ethgov.payments.domain.receipt.ReceiptRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateReceiptUseCase {
    private final ReceiptRepository repository;
    private final Clock clock;

    public CreateReceiptUseCase(ReceiptRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReceiptId execute(CreateReceiptCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Receipt aggregate = Receipt.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateReceiptCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String receiptId,
            String reason) {}
}
