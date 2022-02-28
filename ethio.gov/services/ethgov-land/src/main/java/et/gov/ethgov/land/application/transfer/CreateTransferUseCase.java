package et.gov.ethgov.land.application.transfer;

import et.gov.ethgov.land.domain.transfer.Transfer;
import et.gov.ethgov.land.domain.transfer.TransferId;
import et.gov.ethgov.land.domain.transfer.TransferRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTransferUseCase {
    private final TransferRepository repository;
    private final Clock clock;

    public CreateTransferUseCase(TransferRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TransferId execute(CreateTransferCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Transfer aggregate = Transfer.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTransferCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String transferId,
            String reason) {}
}
