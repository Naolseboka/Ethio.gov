package et.gov.ethgov.permits.application.permitcondition;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionId;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePermitconditionUseCase {
    private final PermitconditionRepository repository;
    private final Clock clock;

    public ActivatePermitconditionUseCase(PermitconditionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitconditionId execute(ActivatePermitconditionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitconditionId id = PermitconditionId.parse(command.permitconditionId());
        Permitcondition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitcondition not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePermitconditionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitconditionId,
            String reason) {}
}
