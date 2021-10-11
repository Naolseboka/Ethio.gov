package et.gov.ethgov.identity.application.passwordpolicy;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyId;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePasswordpolicyUseCase {
    private final PasswordpolicyRepository repository;
    private final Clock clock;

    public UpdatePasswordpolicyUseCase(PasswordpolicyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PasswordpolicyId execute(UpdatePasswordpolicyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PasswordpolicyId id = PasswordpolicyId.parse(command.passwordpolicyId());
        Passwordpolicy aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("passwordpolicy not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePasswordpolicyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String passwordpolicyId,
            String reason) {}
}
