package et.gov.ethgov.licensing.application.holder;

import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderId;
import et.gov.ethgov.licensing.domain.holder.HolderRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateHolderUseCase {
    private final HolderRepository repository;
    private final Clock clock;

    public CreateHolderUseCase(HolderRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HolderId execute(CreateHolderCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Holder aggregate = Holder.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateHolderCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String holderId,
            String reason) {}
}
