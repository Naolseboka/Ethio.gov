package et.gov.ethgov.case.application.priority;

import et.gov.ethgov.case.domain.priority.Priority;
import et.gov.ethgov.case.domain.priority.PriorityId;
import et.gov.ethgov.case.domain.priority.PriorityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePriorityUseCase {
    private final PriorityRepository repository;
    private final Clock clock;

    public ActivatePriorityUseCase(PriorityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PriorityId execute(ActivatePriorityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PriorityId id = PriorityId.parse(command.priorityId());
        Priority aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("priority not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePriorityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String priorityId,
            String reason) {}
}
