package et.gov.ethgov.identity.application.passwordpolicy;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyId;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePasswordpolicyUseCase {
    private final PasswordpolicyRepository repository;
    private final Clock clock;

    public ActivatePasswordpolicyUseCase(PasswordpolicyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PasswordpolicyId execute(ActivatePasswordpolicyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PasswordpolicyId id = PasswordpolicyId.parse(command.passwordpolicyId());
        Passwordpolicy aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("passwordpolicy not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePasswordpolicyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String passwordpolicyId,
            String reason) {}
}
