package et.gov.ethgov.workflow.application.watchlist;

import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistId;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendWatchlistUseCase {
    private final WatchlistRepository repository;
    private final Clock clock;

    public SuspendWatchlistUseCase(WatchlistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WatchlistId execute(SuspendWatchlistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WatchlistId id = WatchlistId.parse(command.watchlistId());
        Watchlist aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("watchlist not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendWatchlistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String watchlistId,
            String reason) {}
}
