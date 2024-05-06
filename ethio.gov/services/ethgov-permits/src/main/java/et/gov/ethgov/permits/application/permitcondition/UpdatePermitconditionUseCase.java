package et.gov.ethgov.permits.application.permitcondition;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionId;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePermitconditionUseCase {
    private final PermitconditionRepository repository;
    private final Clock clock;

    public UpdatePermitconditionUseCase(PermitconditionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitconditionId execute(UpdatePermitconditionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PermitconditionId id = PermitconditionId.parse(command.permitconditionId());
        Permitcondition aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("permitcondition not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePermitconditionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitconditionId,
            String reason) {}
}
