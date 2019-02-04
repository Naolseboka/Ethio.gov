package et.gov.ethgov.case.application.closure;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureId;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendClosureUseCase {
    private final ClosureRepository repository;
    private final Clock clock;

    public SuspendClosureUseCase(ClosureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ClosureId execute(SuspendClosureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ClosureId id = ClosureId.parse(command.closureId());
        Closure aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("closure not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendClosureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String closureId,
            String reason) {}
}
