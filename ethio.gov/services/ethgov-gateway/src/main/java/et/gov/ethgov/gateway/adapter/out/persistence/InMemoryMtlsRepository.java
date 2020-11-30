package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsId;
import et.gov.ethgov.gateway.domain.mtls.MtlsRepository;
import et.gov.ethgov.gateway.domain.mtls.MtlsStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Mtls (replaceable with JPA). */
public final class InMemoryMtlsRepository implements MtlsRepository {
    private final Map<UUID, StoredMtls> store = new ConcurrentHashMap<>();

    @Override
    public Mtls save(Mtls aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredMtls.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Mtls> findById(MtlsId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredMtls::toDomain);
    }

    @Override
    public Optional<Mtls> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredMtls::toDomain)
                .findFirst();
    }

    @Override
    public List<Mtls> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredMtls row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredMtls::toDomain)
                .toList();
    }

    @Override
    public void delete(MtlsId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredMtls(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredMtls from(Mtls aggregate) {
            return new StoredMtls(
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

        Mtls toDomain() {
            return Mtls.rehydrate(
                    MtlsId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    MtlsStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
