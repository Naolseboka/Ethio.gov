package et.gov.ethgov.workflow.application.watchlist;

import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistId;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistRepository;
import java.util.Objects;

public final class GetWatchlistUseCase {
    private final WatchlistRepository repository;

    public GetWatchlistUseCase(WatchlistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Watchlist execute(String rawId) {
        WatchlistId id = WatchlistId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("watchlist not found: " + rawId));
    }
}
