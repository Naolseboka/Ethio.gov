package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.apikey.Apikey;
import et.gov.ethgov.identity.domain.apikey.ApikeyId;
import et.gov.ethgov.identity.domain.apikey.ApikeyRepository;
import et.gov.ethgov.identity.domain.apikey.ApikeyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Apikey (replaceable with JPA). */
public final class InMemoryApikeyRepository implements ApikeyRepository {
    private final Map<UUID, StoredApikey> store = new ConcurrentHashMap<>();

    @Override
    public Apikey save(Apikey aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredApikey.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Apikey> findById(ApikeyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredApikey::toDomain);
    }

    @Override
    public Optional<Apikey> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredApikey::toDomain)
                .findFirst();
    }

    @Override
    public List<Apikey> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredApikey row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredApikey::toDomain)
                .toList();
    }

    @Override
    public void delete(ApikeyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredApikey(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredApikey from(Apikey aggregate) {
            return new StoredApikey(
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

        Apikey toDomain() {
            return Apikey.rehydrate(
                    ApikeyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ApikeyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
