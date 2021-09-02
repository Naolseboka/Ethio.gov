package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.passwordpolicy.Passwordpolicy;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyId;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyRepository;
import et.gov.ethgov.identity.domain.passwordpolicy.PasswordpolicyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Passwordpolicy (replaceable with JPA). */
public final class InMemoryPasswordpolicyRepository implements PasswordpolicyRepository {
    private final Map<UUID, StoredPasswordpolicy> store = new ConcurrentHashMap<>();

    @Override
    public Passwordpolicy save(Passwordpolicy aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredPasswordpolicy.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Passwordpolicy> findById(PasswordpolicyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredPasswordpolicy::toDomain);
    }

    @Override
    public Optional<Passwordpolicy> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredPasswordpolicy::toDomain)
                .findFirst();
    }

    @Override
    public List<Passwordpolicy> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredPasswordpolicy row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredPasswordpolicy::toDomain)
                .toList();
    }

    @Override
    public void delete(PasswordpolicyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredPasswordpolicy(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredPasswordpolicy from(Passwordpolicy aggregate) {
            return new StoredPasswordpolicy(
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

        Passwordpolicy toDomain() {
            return Passwordpolicy.rehydrate(
                    PasswordpolicyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PasswordpolicyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
