package et.gov.ethgov.identity.application.lockout;

import et.gov.ethgov.identity.domain.lockout.Lockout;
import et.gov.ethgov.identity.domain.lockout.LockoutId;
import et.gov.ethgov.identity.domain.lockout.LockoutRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLockoutUseCase {
    private final LockoutRepository repository;
    private final Clock clock;

    public CreateLockoutUseCase(LockoutRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LockoutId execute(CreateLockoutCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Lockout aggregate = Lockout.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLockoutCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String lockoutId,
            String reason) {}
}
