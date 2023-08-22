package et.gov.ethgov.organization.application.successor;

import et.gov.ethgov.organization.domain.successor.Successor;
import et.gov.ethgov.organization.domain.successor.SuccessorId;
import et.gov.ethgov.organization.domain.successor.SuccessorRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendSuccessorUseCase {
    private final SuccessorRepository repository;
    private final Clock clock;

    public SuspendSuccessorUseCase(SuccessorRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SuccessorId execute(SuspendSuccessorCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SuccessorId id = SuccessorId.parse(command.successorId());
        Successor aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("successor not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendSuccessorCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String successorId,
            String reason) {}
}
