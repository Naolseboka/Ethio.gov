package et.gov.ethgov.land.application.transfer;

import et.gov.ethgov.land.domain.transfer.Transfer;
import et.gov.ethgov.land.domain.transfer.TransferId;
import et.gov.ethgov.land.domain.transfer.TransferRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateTransferUseCase {
    private final TransferRepository repository;
    private final Clock clock;

    public ActivateTransferUseCase(TransferRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TransferId execute(ActivateTransferCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TransferId id = TransferId.parse(command.transferId());
        Transfer aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("transfer not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateTransferCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String transferId,
            String reason) {}
}
