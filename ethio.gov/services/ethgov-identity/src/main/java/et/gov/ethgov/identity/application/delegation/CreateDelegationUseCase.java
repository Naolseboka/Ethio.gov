package et.gov.ethgov.identity.application.delegation;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationId;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDelegationUseCase {
    private final DelegationRepository repository;
    private final Clock clock;

    public CreateDelegationUseCase(DelegationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DelegationId execute(CreateDelegationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Delegation aggregate = Delegation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDelegationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String delegationId,
            String reason) {}
}
