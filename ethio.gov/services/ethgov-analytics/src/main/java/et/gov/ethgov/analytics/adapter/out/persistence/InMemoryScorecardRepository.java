package et.gov.ethgov.analytics.adapter.out.persistence;

import et.gov.ethgov.analytics.domain.scorecard.Scorecard;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardId;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardRepository;
import et.gov.ethgov.analytics.domain.scorecard.ScorecardStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Scorecard (replaceable with JPA). */
public final class InMemoryScorecardRepository implements ScorecardRepository {
    private final Map<UUID, StoredScorecard> store = new ConcurrentHashMap<>();

    @Override
    public Scorecard save(Scorecard aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredScorecard.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Scorecard> findById(ScorecardId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredScorecard::toDomain);
    }

    @Override
    public Optional<Scorecard> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredScorecard::toDomain)
                .findFirst();
    }

    @Override
    public List<Scorecard> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredScorecard row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredScorecard::toDomain)
                .toList();
    }

    @Override
    public void delete(ScorecardId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredScorecard(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredScorecard from(Scorecard aggregate) {
            return new StoredScorecard(
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

        Scorecard toDomain() {
            return Scorecard.rehydrate(
                    ScorecardId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ScorecardStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
