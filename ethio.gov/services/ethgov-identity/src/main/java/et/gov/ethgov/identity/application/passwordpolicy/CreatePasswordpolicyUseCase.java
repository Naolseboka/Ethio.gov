package et.gov.ethgov.identity.application.passwordpolicy;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyId;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePasswordpolicyUseCase {
    private final PasswordpolicyRepository repository;
    private final Clock clock;

    public CreatePasswordpolicyUseCase(PasswordpolicyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PasswordpolicyId execute(CreatePasswordpolicyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Passwordpolicy aggregate = Passwordpolicy.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePasswordpolicyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String passwordpolicyId,
            String reason) {}
}
