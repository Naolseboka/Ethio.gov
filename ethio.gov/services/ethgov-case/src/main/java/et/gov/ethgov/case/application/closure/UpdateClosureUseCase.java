package et.gov.ethgov.case.application.closure;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureId;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateClosureUseCase {
    private final ClosureRepository repository;
    private final Clock clock;

    public UpdateClosureUseCase(ClosureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ClosureId execute(UpdateClosureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ClosureId id = ClosureId.parse(command.closureId());
        Closure aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("closure not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateClosureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String closureId,
            String reason) {}
}
