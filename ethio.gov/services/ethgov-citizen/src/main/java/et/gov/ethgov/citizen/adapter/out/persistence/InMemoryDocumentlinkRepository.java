package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkId;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkRepository;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Documentlink (replaceable with JPA). */
public final class InMemoryDocumentlinkRepository implements DocumentlinkRepository {
    private final Map<UUID, StoredDocumentlink> store = new ConcurrentHashMap<>();

    @Override
    public Documentlink save(Documentlink aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredDocumentlink.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Documentlink> findById(DocumentlinkId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredDocumentlink::toDomain);
    }

    @Override
    public Optional<Documentlink> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredDocumentlink::toDomain)
                .findFirst();
    }

    @Override
    public List<Documentlink> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredDocumentlink row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredDocumentlink::toDomain)
                .toList();
    }

    @Override
    public void delete(DocumentlinkId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredDocumentlink(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredDocumentlink from(Documentlink aggregate) {
            return new StoredDocumentlink(
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

        Documentlink toDomain() {
            return Documentlink.rehydrate(
                    DocumentlinkId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    DocumentlinkStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
