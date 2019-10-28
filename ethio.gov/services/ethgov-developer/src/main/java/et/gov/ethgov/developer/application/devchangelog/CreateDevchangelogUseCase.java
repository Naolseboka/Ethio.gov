package et.gov.ethgov.developer.application.devchangelog;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogId;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDevchangelogUseCase {
    private final DevchangelogRepository repository;
    private final Clock clock;

    public CreateDevchangelogUseCase(DevchangelogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevchangelogId execute(CreateDevchangelogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Devchangelog aggregate = Devchangelog.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDevchangelogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devchangelogId,
            String reason) {}
}
