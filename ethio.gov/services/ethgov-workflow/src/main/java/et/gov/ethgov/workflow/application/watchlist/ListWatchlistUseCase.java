package et.gov.ethgov.workflow.application.watchlist;

import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistRepository;
import java.util.List;
import java.util.Objects;

public final class ListWatchlistUseCase {
    private final WatchlistRepository repository;

    public ListWatchlistUseCase(WatchlistRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Watchlist> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
