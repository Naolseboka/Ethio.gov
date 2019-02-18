package et.gov.ethgov.case.application.priority;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityId;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePriorityUseCase {
    private final PriorityRepository repository;
    private final Clock clock;

    public CreatePriorityUseCase(PriorityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PriorityId execute(CreatePriorityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Priority aggregate = Priority.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePriorityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String priorityId,
            String reason) {}
}
