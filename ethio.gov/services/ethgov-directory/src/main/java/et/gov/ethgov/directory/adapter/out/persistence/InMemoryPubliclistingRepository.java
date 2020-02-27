package et.gov.ethgov.directory.adapter.out.persistence;

import et.gov.ethgov.directory.domain.publiclisting.Publiclisting;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingId;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingRepository;
import et.gov.ethgov.directory.domain.publiclisting.PubliclistingStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Publiclisting (replaceable with JPA). */
public final class InMemoryPubliclistingRepository implements PubliclistingRepository {
    private final Map<UUID, StoredPubliclisting> store = new ConcurrentHashMap<>();

    @Override
    public Publiclisting save(Publiclisting aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPubliclisting.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Publiclisting> findById(PubliclistingId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPubliclisting::toDomain);
    }

    @Override
    public Optional<Publiclisting> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPubliclisting::toDomain)
                .findFirst();
    }

    @Override
    public List<Publiclisting> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPubliclisting row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPubliclisting::toDomain)
                .toList();
    }

    @Override
    public void delete(PubliclistingId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPubliclisting(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPubliclisting from(Publiclisting aggregate) {
            return new StoredPubliclisting(
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

        Publiclisting toDomain() {
            return Publiclisting.rehydrate(
                    PubliclistingId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PubliclistingStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
