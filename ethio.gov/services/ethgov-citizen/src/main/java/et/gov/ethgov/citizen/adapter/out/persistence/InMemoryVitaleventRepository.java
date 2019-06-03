package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.vitalevent.Vitalevent;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventId;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventRepository;
import et.gov.ethgov.citizen.domain.vitalevent.VitaleventStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Vitalevent (replaceable with JPA). */
public final class InMemoryVitaleventRepository implements VitaleventRepository {
    private final Map<UUID, StoredVitalevent> store = new ConcurrentHashMap<>();

    @Override
    public Vitalevent save(Vitalevent aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredVitalevent.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Vitalevent> findById(VitaleventId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredVitalevent::toDomain);
    }

    @Override
    public Optional<Vitalevent> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredVitalevent::toDomain)
                .findFirst();
    }

    @Override
    public List<Vitalevent> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredVitalevent row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredVitalevent::toDomain)
                .toList();
    }

    @Override
    public void delete(VitaleventId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredVitalevent(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredVitalevent from(Vitalevent aggregate) {
            return new StoredVitalevent(
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

        Vitalevent toDomain() {
            return Vitalevent.rehydrate(
                    VitaleventId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    VitaleventStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
