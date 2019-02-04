package et.gov.ethgov.case.application.closure;

import et.gov.ethgov.case.domain.closure.Closure;
import et.gov.ethgov.case.domain.closure.ClosureId;
import et.gov.ethgov.case.domain.closure.ClosureRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateClosureUseCase {
    private final ClosureRepository repository;
    private final Clock clock;

    public CreateClosureUseCase(ClosureRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ClosureId execute(CreateClosureCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Closure aggregate = Closure.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateClosureCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String closureId,
            String reason) {}
}
