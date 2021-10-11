package et.gov.ethgov.identity.application.recovery;

import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryId;
import et.gov.ethgov.identity.domain.recovery.RecoveryRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRecoveryUseCase {
    private final RecoveryRepository repository;
    private final Clock clock;

    public CreateRecoveryUseCase(RecoveryRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RecoveryId execute(CreateRecoveryCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Recovery aggregate = Recovery.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRecoveryCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String recoveryId,
            String reason) {}
}
