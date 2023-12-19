package et.gov.ethgov.payments.application.receipt;

import et.gov.ethgov.payments.domain.receipt.Receipt;
import et.gov.ethgov.payments.domain.receipt.ReceiptId;
import et.gov.ethgov.payments.domain.receipt.ReceiptRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendReceiptUseCase {
    private final ReceiptRepository repository;
    private final Clock clock;

    public SuspendReceiptUseCase(ReceiptRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ReceiptId execute(SuspendReceiptCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ReceiptId id = ReceiptId.parse(command.receiptId());
        Receipt aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("receipt not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendReceiptCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String receiptId,
            String reason) {}
}
