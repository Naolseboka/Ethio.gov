package et.gov.ethgov.case.application.priority;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityId;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPriorityUseCase {
    private final PriorityRepository repository;
    private final Clock clock;

    public SuspendPriorityUseCase(PriorityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PriorityId execute(SuspendPriorityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PriorityId id = PriorityId.parse(command.priorityId());
        Priority aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("priority not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPriorityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String priorityId,
            String reason) {}
}
