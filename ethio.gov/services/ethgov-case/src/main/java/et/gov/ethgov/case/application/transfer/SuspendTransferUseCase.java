package et.gov.ethgov.case.application.transfer;

import et.gov.ethgov.case.domain.transfer.Transfer;
import et.gov.ethgov.case.domain.transfer.TransferId;
import et.gov.ethgov.case.domain.transfer.TransferRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendTransferUseCase {
    private final TransferRepository repository;
    private final Clock clock;

    public SuspendTransferUseCase(TransferRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TransferId execute(SuspendTransferCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TransferId id = TransferId.parse(command.transferId());
        Transfer aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("transfer not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendTransferCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String transferId,
            String reason) {}
}
