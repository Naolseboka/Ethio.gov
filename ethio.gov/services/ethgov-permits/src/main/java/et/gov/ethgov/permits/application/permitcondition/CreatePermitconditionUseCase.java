package et.gov.ethgov.permits.application.permitcondition;

import et.gov.ethgov.permits.domain.permitcondition.Permitcondition;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionId;
import et.gov.ethgov.permits.domain.permitcondition.PermitconditionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePermitconditionUseCase {
    private final PermitconditionRepository repository;
    private final Clock clock;

    public CreatePermitconditionUseCase(PermitconditionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PermitconditionId execute(CreatePermitconditionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Permitcondition aggregate = Permitcondition.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePermitconditionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String permitconditionId,
            String reason) {}
}
