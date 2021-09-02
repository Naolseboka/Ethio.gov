package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.mfa.Mfa;
import et.gov.ethgov.identity.domain.mfa.MfaId;
import et.gov.ethgov.identity.domain.mfa.MfaRepository;
import et.gov.ethgov.identity.domain.mfa.MfaStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Mfa (replaceable with JPA). */
public final class InMemoryMfaRepository implements MfaRepository {
    private final Map<UUID, StoredMfa> store = new ConcurrentHashMap<>();

    @Override
    public Mfa save(Mfa aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredMfa.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Mfa> findById(MfaId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredMfa::toDomain);
    }

    @Override
    public Optional<Mfa> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredMfa::toDomain)
                .findFirst();
    }

    @Override
    public List<Mfa> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredMfa row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredMfa::toDomain)
                .toList();
    }

    @Override
    public void delete(MfaId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredMfa(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredMfa from(Mfa aggregate) {
            return new StoredMfa(
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

        Mfa toDomain() {
            return Mfa.rehydrate(
                    MfaId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    MfaStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
