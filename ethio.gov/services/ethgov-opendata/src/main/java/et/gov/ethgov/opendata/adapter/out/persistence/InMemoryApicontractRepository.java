package et.gov.ethgov.opendata.adapter.out.persistence;

import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractId;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractRepository;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Apicontract (replaceable with JPA). */
public final class InMemoryApicontractRepository implements ApicontractRepository {
    private final Map<UUID, StoredApicontract> store = new ConcurrentHashMap<>();

    @Override
    public Apicontract save(Apicontract aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredApicontract.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Apicontract> findById(ApicontractId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredApicontract::toDomain);
    }

    @Override
    public Optional<Apicontract> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredApicontract::toDomain)
                .findFirst();
    }

    @Override
    public List<Apicontract> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredApicontract row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredApicontract::toDomain)
                .toList();
    }

    @Override
    public void delete(ApicontractId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredApicontract(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredApicontract from(Apicontract aggregate) {
            return new StoredApicontract(
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

        Apicontract toDomain() {
            return Apicontract.rehydrate(
                    ApicontractId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ApicontractStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
