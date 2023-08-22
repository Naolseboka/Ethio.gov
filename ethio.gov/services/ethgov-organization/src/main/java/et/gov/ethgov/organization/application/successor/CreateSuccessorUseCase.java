package et.gov.ethgov.organization.application.successor;

import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorId;
import et.gov.ethgov.organization.domain.successor.SuccessorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateSuccessorUseCase {
    private final SuccessorRepository repository;
    private final Clock clock;

    public CreateSuccessorUseCase(SuccessorRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuccessorId execute(CreateSuccessorCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Successor aggregate = Successor.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateSuccessorCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String successorId,
            String reason) {}
}
