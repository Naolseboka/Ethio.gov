package et.gov.ethgov.developer.application.devchangelog;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogId;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDevchangelogUseCase {
    private final DevchangelogRepository repository;
    private final Clock clock;

    public UpdateDevchangelogUseCase(DevchangelogRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DevchangelogId execute(UpdateDevchangelogCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DevchangelogId id = DevchangelogId.parse(command.devchangelogId());
        Devchangelog aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devchangelog not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDevchangelogCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String devchangelogId,
            String reason) {}
}
