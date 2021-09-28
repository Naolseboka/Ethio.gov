package et.gov.ethgov.identity.application.lockout;

import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutId;
import et.gov.ethgov.identity.domain.lockout.LockoutRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateLockoutUseCase {
    private final LockoutRepository repository;
    private final Clock clock;

    public UpdateLockoutUseCase(LockoutRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LockoutId execute(UpdateLockoutCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LockoutId id = LockoutId.parse(command.lockoutId());
        Lockout aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("lockout not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateLockoutCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String lockoutId,
            String reason) {}
}
