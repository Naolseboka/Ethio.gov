package et.gov.ethgov.identity.application.recovery;

import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryId;
import et.gov.ethgov.identity.domain.recovery.RecoveryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRecoveryUseCase {
    private final RecoveryRepository repository;
    private final Clock clock;

    public UpdateRecoveryUseCase(RecoveryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RecoveryId execute(UpdateRecoveryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RecoveryId id = RecoveryId.parse(command.recoveryId());
        Recovery aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("recovery not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRecoveryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String recoveryId,
            String reason) {}
}
