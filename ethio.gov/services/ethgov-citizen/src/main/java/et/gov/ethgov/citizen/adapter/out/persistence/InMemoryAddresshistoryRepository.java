package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.addresshistory.Addresshistory;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryId;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryRepository;
import et.gov.ethgov.citizen.domain.addresshistory.AddresshistoryStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Addresshistory (replaceable with JPA). */
public final class InMemoryAddresshistoryRepository implements AddresshistoryRepository {
    private final Map<UUID, StoredAddresshistory> store = new ConcurrentHashMap<>();

    @Override
    public Addresshistory save(Addresshistory aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAddresshistory.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Addresshistory> findById(AddresshistoryId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAddresshistory::toDomain);
    }

    @Override
    public Optional<Addresshistory> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAddresshistory::toDomain)
                .findFirst();
    }

    @Override
    public List<Addresshistory> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAddresshistory row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAddresshistory::toDomain)
                .toList();
    }

    @Override
    public void delete(AddresshistoryId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAddresshistory(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAddresshistory from(Addresshistory aggregate) {
            return new StoredAddresshistory(
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

        Addresshistory toDomain() {
            return Addresshistory.rehydrate(
                    AddresshistoryId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AddresshistoryStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
