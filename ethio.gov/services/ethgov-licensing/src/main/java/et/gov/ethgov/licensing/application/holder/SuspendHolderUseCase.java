package et.gov.ethgov.licensing.application.holder;

import et.gov.ethgov.licensing.domain.holder.Holder;
import et.gov.ethgov.licensing.domain.holder.HolderId;
import et.gov.ethgov.licensing.domain.holder.HolderRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendHolderUseCase {
    private final HolderRepository repository;
    private final Clock clock;

    public SuspendHolderUseCase(HolderRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public HolderId execute(SuspendHolderCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        HolderId id = HolderId.parse(command.holderId());
        Holder aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("holder not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendHolderCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String holderId,
            String reason) {}
}
