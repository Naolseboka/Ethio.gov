package et.gov.ethgov.developer.adapter.out.persistence;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketId;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import et.gov.ethgov.developer.domain.supportticket.SupportticketStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Supportticket (replaceable with JPA). */
public final class InMemorySupportticketRepository implements SupportticketRepository {
    private final Map<UUID, StoredSupportticket> store = new ConcurrentHashMap<>();

    @Override
    public Supportticket save(Supportticket aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredSupportticket.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Supportticket> findById(SupportticketId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredSupportticket::toDomain);
    }

    @Override
    public Optional<Supportticket> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredSupportticket::toDomain)
                .findFirst();
    }

    @Override
    public List<Supportticket> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredSupportticket row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredSupportticket::toDomain)
                .toList();
    }

    @Override
    public void delete(SupportticketId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredSupportticket(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredSupportticket from(Supportticket aggregate) {
            return new StoredSupportticket(
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

        Supportticket toDomain() {
            return Supportticket.rehydrate(
                    SupportticketId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    SupportticketStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
