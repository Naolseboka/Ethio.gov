package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.encumbrance.Encumbrance;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceId;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceRepository;
import et.gov.ethgov.land.domain.encumbrance.EncumbranceStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Encumbrance (replaceable with JPA). */
public final class InMemoryEncumbranceRepository implements EncumbranceRepository {
    private final Map<UUID, StoredEncumbrance> store = new ConcurrentHashMap<>();

    @Override
    public Encumbrance save(Encumbrance aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredEncumbrance.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Encumbrance> findById(EncumbranceId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredEncumbrance::toDomain);
    }

    @Override
    public Optional<Encumbrance> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredEncumbrance::toDomain)
                .findFirst();
    }

    @Override
    public List<Encumbrance> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredEncumbrance row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredEncumbrance::toDomain)
                .toList();
    }

    @Override
    public void delete(EncumbranceId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredEncumbrance(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredEncumbrance from(Encumbrance aggregate) {
            return new StoredEncumbrance(
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

        Encumbrance toDomain() {
            return Encumbrance.rehydrate(
                    EncumbranceId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    EncumbranceStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
