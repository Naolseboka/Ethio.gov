package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.devcredential.Devcredential;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialId;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialRepository;
import et.gov.ethgov.developer.domain.devcredential.DevcredentialStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Devcredential (replaceable with JPA). */
public final class InMemoryDevcredentialRepository implements DevcredentialRepository {
    private final Map<UUID, StoredDevcredential> store = new ConcurrentHashMap<>();

    @Override
    public Devcredential save(Devcredential aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDevcredential.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Devcredential> findById(DevcredentialId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDevcredential::toDomain);
    }

    @Override
    public Optional<Devcredential> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDevcredential::toDomain)
                .findFirst();
    }

    @Override
    public List<Devcredential> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDevcredential row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDevcredential::toDomain)
                .toList();
    }

    @Override
    public void delete(DevcredentialId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDevcredential(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDevcredential from(Devcredential aggregate) {
            return new StoredDevcredential(
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

        Devcredential toDomain() {
            return Devcredential.rehydrate(
                    DevcredentialId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DevcredentialStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
