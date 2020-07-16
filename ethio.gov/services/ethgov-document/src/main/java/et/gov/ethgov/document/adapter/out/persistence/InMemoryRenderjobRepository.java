package et.gov.ethgov.document.adapter.out.persistence;

import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobId;
import et.gov.ethgov.document.domain.renderjob.RenderjobRepository;
import et.gov.ethgov.document.domain.renderjob.RenderjobStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Renderjob (replaceable with JPA). */
public final class InMemoryRenderjobRepository implements RenderjobRepository {
    private final Map<UUID, StoredRenderjob> store = new ConcurrentHashMap<>();

    @Override
    public Renderjob save(Renderjob aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredRenderjob.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Renderjob> findById(RenderjobId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredRenderjob::toDomain);
    }

    @Override
    public Optional<Renderjob> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredRenderjob::toDomain)
                .findFirst();
    }

    @Override
    public List<Renderjob> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredRenderjob row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredRenderjob::toDomain)
                .toList();
    }

    @Override
    public void delete(RenderjobId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredRenderjob(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredRenderjob from(Renderjob aggregate) {
            return new StoredRenderjob(
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

        Renderjob toDomain() {
            return Renderjob.rehydrate(
                    RenderjobId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    RenderjobStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
