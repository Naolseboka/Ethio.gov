package et.gov.ethgov.gis.application.tilecache;

import et.gov.ethgov.gis.domain.tilecache.Tilecache;
import et.gov.ethgov.gis.domain.tilecache.TilecacheId;
import et.gov.ethgov.gis.domain.tilecache.TilecacheRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendTilecacheUseCase {
    private final TilecacheRepository repository;
    private final Clock clock;

    public SuspendTilecacheUseCase(TilecacheRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TilecacheId execute(SuspendTilecacheCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        TilecacheId id = TilecacheId.parse(command.tilecacheId());
        Tilecache aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tilecache not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendTilecacheCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String tilecacheId,
            String reason) {}
}
