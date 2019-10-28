package et.gov.ethgov.developer.application.devchangelog;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogId;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateDevchangelogUseCase {
    private final DevchangelogRepository repository;
    private final Clock clock;

    public ActivateDevchangelogUseCase(DevchangelogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevchangelogId execute(ActivateDevchangelogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevchangelogId id = DevchangelogId.parse(command.devchangelogId());
        Devchangelog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devchangelog not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateDevchangelogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devchangelogId,
            String reason) {}
}
