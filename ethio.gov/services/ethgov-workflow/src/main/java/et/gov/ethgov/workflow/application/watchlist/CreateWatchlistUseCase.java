package et.gov.ethgov.workflow.application.watchlist;

import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistId;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWatchlistUseCase {
    private final WatchlistRepository repository;
    private final Clock clock;

    public CreateWatchlistUseCase(WatchlistRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WatchlistId execute(CreateWatchlistCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Watchlist aggregate = Watchlist.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWatchlistCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String watchlistId,
            String reason) {}
}
