package et.gov.ethgov.search.adapter.out.persistence;

import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingId;
import et.gov.ethgov.search.domain.ranking.RankingRepository;
import et.gov.ethgov.search.domain.ranking.RankingStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Ranking (replaceable with JPA). */
public final class InMemoryRankingRepository implements RankingRepository {
    private final Map<UUID, StoredRanking> store = new ConcurrentHashMap<>();

    @Override
    public Ranking save(Ranking aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRanking.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Ranking> findById(RankingId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRanking::toDomain);
    }

    @Override
    public Optional<Ranking> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRanking::toDomain)
                .findFirst();
    }

    @Override
    public List<Ranking> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRanking row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRanking::toDomain)
                .toList();
    }

    @Override
    public void delete(RankingId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRanking(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRanking from(Ranking aggregate) {
            return new StoredRanking(
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

        Ranking toDomain() {
            return Ranking.rehydrate(
                    RankingId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RankingStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
