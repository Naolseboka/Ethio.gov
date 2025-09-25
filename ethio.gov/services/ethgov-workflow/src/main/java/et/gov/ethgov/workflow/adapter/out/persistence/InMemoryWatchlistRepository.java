package et.gov.ethgov.workflow.adapter.out.persistence;

import et.gov.ethgov.workflow.domain.watchlist.Watchlist;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistId;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistRepository;
import et.gov.ethgov.workflow.domain.watchlist.WatchlistStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Watchlist (replaceable with JPA). */
public final class InMemoryWatchlistRepository implements WatchlistRepository {
    private final Map<UUID, StoredWatchlist> store = new ConcurrentHashMap<>();

    @Override
    public Watchlist save(Watchlist aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredWatchlist.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Watchlist> findById(WatchlistId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredWatchlist::toDomain);
    }

    @Override
    public Optional<Watchlist> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredWatchlist::toDomain)
                .findFirst();
    }

    @Override
    public List<Watchlist> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredWatchlist row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredWatchlist::toDomain)
                .toList();
    }

    @Override
    public void delete(WatchlistId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredWatchlist(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredWatchlist from(Watchlist aggregate) {
            return new StoredWatchlist(
                    aggregate.id().value(),
                    aggregate.referenceCode(),
                    aggregate.title(),
                    aggregate.description(),
                    aggregate.tenantId(),
                    aggregate.status().name(),
                    aggregate.createdAt(),
                    aggregate.updatedAt(),
                    aggregate.version());
        }

        Watchlist toDomain() {
            return Watchlist.rehydrate(
                    WatchlistId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    WatchlistStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
