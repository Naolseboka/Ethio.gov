package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.riskscore.Riskscore;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreId;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreRepository;
import et.gov.ethgov.identity.domain.riskscore.RiskscoreStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Riskscore (replaceable with JPA). */
public final class InMemoryRiskscoreRepository implements RiskscoreRepository {
    private final Map<UUID, StoredRiskscore> store = new ConcurrentHashMap<>();

    @Override
    public Riskscore save(Riskscore aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRiskscore.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Riskscore> findById(RiskscoreId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRiskscore::toDomain);
    }

    @Override
    public Optional<Riskscore> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRiskscore::toDomain)
                .findFirst();
    }

    @Override
    public List<Riskscore> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRiskscore row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRiskscore::toDomain)
                .toList();
    }

    @Override
    public void delete(RiskscoreId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRiskscore(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRiskscore from(Riskscore aggregate) {
            return new StoredRiskscore(
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

        Riskscore toDomain() {
            return Riskscore.rehydrate(
                    RiskscoreId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RiskscoreStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
