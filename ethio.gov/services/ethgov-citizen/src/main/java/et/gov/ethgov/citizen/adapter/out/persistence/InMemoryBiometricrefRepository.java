package et.gov.ethgov.citizen.adapter.out.persistence;

import et.gov.ethgov.citizen.domain.biometricref.Biometricref;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefId;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefRepository;
import et.gov.ethgov.citizen.domain.biometricref.BiometricrefStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Biometricref (replaceable with JPA). */
public final class InMemoryBiometricrefRepository implements BiometricrefRepository {
    private final Map<UUID, StoredBiometricref> store = new ConcurrentHashMap<>();

    @Override
    public Biometricref save(Biometricref aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredBiometricref.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Biometricref> findById(BiometricrefId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredBiometricref::toDomain);
    }

    @Override
    public Optional<Biometricref> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredBiometricref::toDomain)
                .findFirst();
    }

    @Override
    public List<Biometricref> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredBiometricref row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredBiometricref::toDomain)
                .toList();
    }

    @Override
    public void delete(BiometricrefId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredBiometricref(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredBiometricref from(Biometricref aggregate) {
            return new StoredBiometricref(
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

        Biometricref toDomain() {
            return Biometricref.rehydrate(
                    BiometricrefId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    BiometricrefStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
