package et.gov.ethgov.revenue.adapter.out.persistence;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsId;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import et.gov.ethgov.revenue.domain.arrears.ArrearsStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Arrears (replaceable with JPA). */
public final class InMemoryArrearsRepository implements ArrearsRepository {
    private final Map<UUID, StoredArrears> store = new ConcurrentHashMap<>();

    @Override
    public Arrears save(Arrears aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredArrears.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Arrears> findById(ArrearsId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredArrears::toDomain);
    }

    @Override
    public Optional<Arrears> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredArrears::toDomain)
                .findFirst();
    }

    @Override
    public List<Arrears> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredArrears row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredArrears::toDomain)
                .toList();
    }

    @Override
    public void delete(ArrearsId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredArrears(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredArrears from(Arrears aggregate) {
            return new StoredArrears(
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

        Arrears toDomain() {
            return Arrears.rehydrate(
                    ArrearsId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ArrearsStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
