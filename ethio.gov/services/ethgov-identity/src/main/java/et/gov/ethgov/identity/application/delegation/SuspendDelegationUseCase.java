package et.gov.ethgov.identity.application.delegation;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationId;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDelegationUseCase {
    private final DelegationRepository repository;
    private final Clock clock;

    public SuspendDelegationUseCase(DelegationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DelegationId execute(SuspendDelegationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DelegationId id = DelegationId.parse(command.delegationId());
        Delegation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delegation not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDelegationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String delegationId,
            String reason) {}
}
