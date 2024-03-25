package et.gov.ethgov.permits.adapter.out.persistence;

import et.gov.ethgov.permits.domain.bond.Bond;
import et.gov.ethgov.permits.domain.bond.BondId;
import et.gov.ethgov.permits.domain.bond.BondRepository;
import et.gov.ethgov.permits.domain.bond.BondStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Bond (replaceable with JPA). */
public final class InMemoryBondRepository implements BondRepository {
    private final Map<UUID, StoredBond> store = new ConcurrentHashMap<>();

    @Override
    public Bond save(Bond aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBond.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Bond> findById(BondId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBond::toDomain);
    }

    @Override
    public Optional<Bond> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBond::toDomain)
                .findFirst();
    }

    @Override
    public List<Bond> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBond row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBond::toDomain)
                .toList();
    }

    @Override
    public void delete(BondId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBond(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBond from(Bond aggregate) {
            return new StoredBond(
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

        Bond toDomain() {
            return Bond.rehydrate(
                    BondId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BondStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
