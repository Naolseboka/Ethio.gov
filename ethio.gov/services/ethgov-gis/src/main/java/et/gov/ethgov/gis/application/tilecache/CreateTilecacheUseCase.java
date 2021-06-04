package et.gov.ethgov.gis.application.tilecache;

import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheId;
import et.gov.ethgov.gis.domain.tilecache.TilecacheRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateTilecacheUseCase {
    private final TilecacheRepository repository;
    private final Clock clock;

    public CreateTilecacheUseCase(TilecacheRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TilecacheId execute(CreateTilecacheCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Tilecache aggregate = Tilecache.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateTilecacheCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String tilecacheId,
            String reason) {}
}
