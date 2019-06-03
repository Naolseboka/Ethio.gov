package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeId;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import et.gov.ethgov.citizen.domain.namechange.NamechangeStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Namechange (replaceable with JPA). */
public final class InMemoryNamechangeRepository implements NamechangeRepository {
    private final Map<UUID, StoredNamechange> store = new ConcurrentHashMap<>();

    @Override
    public Namechange save(Namechange aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredNamechange.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Namechange> findById(NamechangeId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredNamechange::toDomain);
    }

    @Override
    public Optional<Namechange> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredNamechange::toDomain)
                .findFirst();
    }

    @Override
    public List<Namechange> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredNamechange row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredNamechange::toDomain)
                .toList();
    }

    @Override
    public void delete(NamechangeId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredNamechange(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredNamechange from(Namechange aggregate) {
            return new StoredNamechange(
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

        Namechange toDomain() {
            return Namechange.rehydrate(
                    NamechangeId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    NamechangeStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
