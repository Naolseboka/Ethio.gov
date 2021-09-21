package et.gov.ethgov.identity.application.delegation;

import et.gov.ethgov.identity.domain.delegation.Delegation;
import et.gov.ethgov.identity.domain.delegation.DelegationId;
import et.gov.ethgov.identity.domain.delegation.DelegationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDelegationUseCase {
    private final DelegationRepository repository;
    private final Clock clock;

    public UpdateDelegationUseCase(DelegationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DelegationId execute(UpdateDelegationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DelegationId id = DelegationId.parse(command.delegationId());
        Delegation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("delegation not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDelegationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String delegationId,
            String reason) {}
}
