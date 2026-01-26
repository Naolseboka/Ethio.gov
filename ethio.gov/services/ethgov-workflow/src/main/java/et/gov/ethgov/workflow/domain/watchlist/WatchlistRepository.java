package et.gov.ethgov.workflow.domain.watchlist;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository {
    Watchlist save(Watchlist aggregate);

    Optional<Watchlist> findById(WatchlistId id);

    Optional<Watchlist> findByReferenceCode(String tenantId, String referenceCode);

    List<Watchlist> findByTenant(String tenantId, int limit);

    void delete(WatchlistId id);
}
